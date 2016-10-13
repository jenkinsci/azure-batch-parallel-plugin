/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.azurebatch.jobgen;

import com.microsoft.azure.batch.protocol.models.TaskAddParameter;
import com.microsoft.azure.batch.protocol.models.TaskConstraints;
import com.microsoft.azurebatch.jenkins.TestInParallelPostBuild;
import com.microsoft.azurebatch.jenkins.azurebatch.TaskDefinition;
import com.microsoft.azurebatch.jenkins.logger.Logger;
import com.microsoft.azurebatch.jenkins.resource.LocalResourceEntity;
import com.microsoft.azurebatch.jenkins.resource.ResourceEntity;
import com.microsoft.windowsazure.storage.StorageException;
import org.joda.time.Period;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.microsoft.azurebatch.jenkins.azurebatch.jobgen.JobGenerator.staticScriptResourceFolder;

/**
 * Class to create Batch job running on Linux VMs.
 */
public class JobGeneratorLinux extends JobGenerator {
    
    private static final String vmSetupFileName = "VMSetup.sh";
    
    private static final String lineSeperator = "\n";

    @Override
    protected String getTaskPostProcessFileName() {
        return "TaskPostProcess.sh";
    }

    @Override
    protected TaskAddParameter getTaskAddParameterFromDefinition(TaskDefinition taskDefinition) throws IOException, URISyntaxException, StorageException, IllegalArgumentException, InterruptedException, InvalidKeyException {
        List<String> resultFilePatterns = projectConfigHelper.getTestConfigs().getResultFilePatterns();
        String taskId = taskDefinition.getTaskId();

        TaskAddParameter taskAddParam = new TaskAddParameter();
        taskAddParam.withId(taskId)
                .withDisplayName(taskDefinition.getName());

        List<String> commandLineList = new ArrayList<>();

        for (String command : taskDefinition.getCommands()) {
            commandLineList.add(String.format("%s >> $AZ_BATCH_TASK_WORKING_DIR/task_stdout.txt 2>>$AZ_BATCH_TASK_WORKING_DIR/task_stderr.txt", command));
        }
        
        // Go back to $AZ_BATCH_TASK_WORKING_DIR in case customer may cd to other folder
        commandLineList.add("cd $AZ_BATCH_TASK_WORKING_DIR");        
        
        // Copy results to a temp folder
        List<String> resultFilePatternList = new ArrayList<>(resultFilePatterns);

        // Copy all result files to randomUUID_ResultFolder\taskId\ folder.
        String tempResultFolderName = "tempResults" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        commandLineList.add(String.format("mkdir -p %s/%s", tempResultFolderName, taskId));
        for (String resultFilePattern : resultFilePatternList) {
            // Copy results and ignore failures
            commandLineList.add(String.format("cp -f %s %s/%s 2>null", resultFilePattern, tempResultFolderName, taskId));
        }
        
        // Call taskPostProcessFile
        commandLineList.add(String.format("/bin/sh %s %s %s %s \"%s\" %s", 
                getTaskPostProcessFileName(), jobId, taskId, storageAccountInfo.getAccountName(), 
                containerSasKey, tempResultFolderName));

        String workPathCmd = scriptTempFolder + File.separator + taskId + ".sh";
        Path p = Paths.get(workPathCmd);
        if (Files.exists(p)) {
            Files.delete(p);
        }

        // Generate the task command line at run time based on the dll
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(workPathCmd), false), java.nio.charset.Charset.defaultCharset())) {
            for (String line : commandLineList) {
                writer.write(line);
                writer.write(lineSeperator);
            }
        } catch (IOException e) {
            Logger.log(listener, "Task command file generation failed");
            Logger.log(listener, e.getMessage());
            throw e;
        }
        
        TaskConstraints taskConstraints = new TaskConstraints();
        taskConstraints.withMaxWallClockTime(Period.minutes(taskDefinition.getTimeoutInMins()));        

        taskAddParam.withCommandLine(String.format(
                "/bin/sh -c 'cp %s/scripts/%s.sh ./ && cp %s/scripts/%s ./ && /bin/sh %s.sh'",
                "$AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID", taskId, "$AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID", getTaskPostProcessFileName(), taskId));
        taskAddParam.withConstraints(taskConstraints);

        return taskAddParam;
    }    

    @Override
    protected void addSharedResourcesForJobPreparationTask() throws IOException {
        // Resource to install blobxfer on target VM
        String installPythonFileName = scriptTempFolder + File.separator + "JobPrepareInstall.sh";
        Files.copy(TestInParallelPostBuild.class.getResourceAsStream(staticScriptResourceFolder + "JobPrepareInstall.sh"), Paths.get(installPythonFileName));       
        LocalResourceEntity resourceInstallDependencies = new LocalResourceEntity(installPythonFileName);
        
        // Resource of Python script to unzip on target VM
        String unzipPythonFileName = scriptTempFolder + File.separator + "Zip.py";
        Files.copy(TestInParallelPostBuild.class.getResourceAsStream(staticScriptResourceFolder + "Zip.py"), Paths.get(unzipPythonFileName));       
        sharedResourceEntityList.add(new LocalResourceEntity(unzipPythonFileName));
                        
        // Generate BatchVMSetup.sh script content
        List<String> commandLineList = new ArrayList<>();   
        
        // Install dependencies
        commandLineList.add(String.format("/bin/sh ./%s", resourceInstallDependencies.getResourceName()));     
        
        // Create folder $AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID for resources
        commandLineList.add("mkdir -p $AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID");

        // Extract resources, which may require Python to unzip.
        for (ResourceEntity resource : sharedResourceEntityList) {
            commandLineList.add(getExtractResourceEntityCommand(resource));
        }
        
        // Allow all to read files under $AZ_BATCH_NODE_SHARED_DIR
        commandLineList.add("chmod -R a+r $AZ_BATCH_NODE_SHARED_DIR/*");
                
        sharedResourceEntityList.add(resourceInstallDependencies);
        
        String customerVMSetupCommandLine = this.projectConfigHelper.getVMConfigs().getVmSetupCommandLine();
                        
        customerVMSetupCommandLine = String.format("%s > JobPreparationTask_stdout.txt 2>JobPreparationTask_stderr.txt", customerVMSetupCommandLine);
        commandLineList.add(customerVMSetupCommandLine);

        // Add VMSetup.cmd script as ResourceEntity
        String vmSetupScriptFileName = scriptTempFolder + File.separator + vmSetupFileName;        
        try (Writer vmSetupFileWriter = new OutputStreamWriter(new FileOutputStream(new File(vmSetupScriptFileName), false), java.nio.charset.Charset.defaultCharset())) {
            for (String line : commandLineList) {
                vmSetupFileWriter.write(line);
                vmSetupFileWriter.write(lineSeperator);
            }
        }
        
        LocalResourceEntity resourceVMSetupCmdFile = new LocalResourceEntity(vmSetupScriptFileName);
        sharedResourceEntityList.add(resourceVMSetupCmdFile);
    }

    @Override
    protected String getJobPreparationTaskCommandLine() {
        return String.format("/bin/sh ./%s", vmSetupFileName);
    }
    
    private String getExtractResourceEntityCommand(ResourceEntity resource) {
        // @resource is in JobPrep task folder. The actual file is resource.getBlobName(), 
        // we need to extract it if it's zipped folder, and copy to $AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID.
        final String binaryFolderOnVM = "$AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID";
        if (resource.requireUnzip()) {
            // resource.getBlobName() is FolderName.zip, extract it to $AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID
            return String.format("python Zip.py unzip %s %s", resource.getBlobName(), binaryFolderOnVM);
        } else {
            // Simply copy resource.getBlobName() to %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID%
            return String.format("cp -f %s %s", resource.getBlobName(), binaryFolderOnVM);
        }
    }
}
