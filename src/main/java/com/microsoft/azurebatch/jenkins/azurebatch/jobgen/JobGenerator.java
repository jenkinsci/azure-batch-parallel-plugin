/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.azurebatch.jobgen;

import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.BatchClientBehavior;
import com.microsoft.azure.batch.interceptor.BatchClientParallelOptions;
import com.microsoft.azure.batch.protocol.models.*;
import com.microsoft.azurebatch.jenkins.TestInParallelPostBuild;
import com.microsoft.azurebatch.jenkins.azurebatch.TaskDefinition;
import com.microsoft.azurebatch.jenkins.azurestorage.AzureStorageHelper;
import com.microsoft.azurebatch.jenkins.azurestorage.StorageAccountInfo;
import com.microsoft.azurebatch.jenkins.jobsplitter.JobSplitterHelper;
import com.microsoft.azurebatch.jenkins.logger.Logger;
import com.microsoft.azurebatch.jenkins.projectconfig.ProjectConfigHelper;
import com.microsoft.azurebatch.jenkins.resource.LocalResourceEntity;
import com.microsoft.azurebatch.jenkins.resource.ResourceEntity;
import com.microsoft.azurebatch.jenkins.resource.ResourceEntityHelper;
import com.microsoft.azurebatch.jenkins.utils.Utils;
import com.microsoft.azurebatch.jenkins.utils.WorkspaceHelper;
import com.microsoft.windowsazure.storage.StorageException;
import com.microsoft.windowsazure.storage.blob.CloudBlobContainer;
import hudson.model.BuildListener;
import org.joda.time.Period;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
    
/**
 * Class to create Batch job
 */
public abstract class JobGenerator {
    
    protected BuildListener listener;
    protected WorkspaceHelper workspaceHelper;
    protected ProjectConfigHelper projectConfigHelper;
    protected JobSplitterHelper jobSplitterHelper;
    protected List<ResourceEntity> sharedResourceEntityList;
    protected BatchClient client;
    protected String scriptTempFolder;
    protected String jobId;
    protected String poolId;
    
    protected StorageAccountInfo storageAccountInfo;
    protected String containerSasKey;
    
    protected static final String staticScriptResourceFolder = "TestInParallelPostBuild/scripts/";
    
    public JobGenerator() {
    }
    
    public void initialize(BuildListener listener, WorkspaceHelper workspaceHelper,
            ProjectConfigHelper projectConfigHelper, JobSplitterHelper jobSplitterHelper, List<ResourceEntity> sharedResourceEntityList,
            BatchClient client, String jobId, String poolId,
            StorageAccountInfo storageAccountInfo, String containerSasKey) throws IOException {
        this.listener = listener;
        this.workspaceHelper = workspaceHelper;
        this.projectConfigHelper = projectConfigHelper;
        this.jobSplitterHelper = jobSplitterHelper;
        this.sharedResourceEntityList = sharedResourceEntityList;
        this.client = client;
        this.jobId = jobId;
        this.poolId = poolId;
        this.storageAccountInfo = storageAccountInfo;
        this.containerSasKey = containerSasKey;
        
        scriptTempFolder = workspaceHelper.getPathRelativeToTempFolder("scripts");
        if (!Utils.dirExists(scriptTempFolder)) {
            Files.createDirectory(Paths.get(scriptTempFolder));
        }
    }
    
    /**
     * Create Batch job with tasks
     * @param jobTimeoutInMins Timeout in minutes of the job, see more <a href="https://azure.microsoft.com/documentation/articles/batch-api-basics/">here</a>
     * @throws URISyntaxException
     * @throws StorageException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     * @throws BatchErrorException 
     */
    public void createJobWithTasks(int jobTimeoutInMins) throws IOException, BatchErrorException, InterruptedException, URISyntaxException, StorageException, IllegalArgumentException, InvalidKeyException {
        // Copy post process file to script folder before creating job preparation task
        try (InputStream resoureceStream = TestInParallelPostBuild.class.getResourceAsStream(staticScriptResourceFolder + getTaskPostProcessFileName())) {
            // Copy TaskPostProcessFile to scriptTempFolder
            Files.copy(resoureceStream, Paths.get(scriptTempFolder + File.separator + getTaskPostProcessFileName()));   
        }
        
        // Create task list before creating job preparation task
        List<TaskAddParameter> taskList = createTaskList();
                
        // Create JobPrep task which be running on VM as VM setup task.
        JobPreparationTask jobPreparationTask = createJobPreparationTask();

        // Create the actual job with tasks using pool with poolId.        
        createJobWithTasks(jobTimeoutInMins, jobPreparationTask, taskList);
    }
    
    /**
     * Get TaskPostProcess file name
     * @return TaskPostProcess file name
     */
    protected abstract String getTaskPostProcessFileName();
    
    /**
     * Get TaskAddParameter from TaskDefinition
     * @param taskDefinition Task definition
     * @return TaskAddParameter
     * @throws IOException
     * @throws URISyntaxException
     * @throws StorageException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     * @throws InvalidKeyException 
     */
    protected abstract TaskAddParameter getTaskAddParameterFromDefinition(TaskDefinition taskDefinition) 
            throws IOException, URISyntaxException, StorageException, IllegalArgumentException, InterruptedException, InvalidKeyException;
        
    /**
     * Add all resources to sharedResourceEntityList
     * @throws IOException 
     */
    protected abstract void addSharedResourcesForJobPreparationTask() throws IOException;
    
    /**
     * Get JobPreparationTask command line
     * @return JobPreparationTask command line
     */
    protected abstract String getJobPreparationTaskCommandLine();
    
    private void createJobWithTasks(
            int jobCompletedTimeoutInMin,            
            JobPreparationTask jobPreparationTask,
            List<TaskAddParameter> taskList) throws BatchErrorException, IOException, InterruptedException, URISyntaxException, StorageException, IllegalArgumentException, InvalidKeyException {  
        Logger.log(listener, "Create job %s with pool: %s", jobId, poolId);
                    
        PoolInformation poolInformation = new PoolInformation();
        poolInformation.withPoolId(poolId);
        
        JobConstraints jobConstraints = new JobConstraints();
        jobConstraints.withMaxWallClockTime(Period.minutes(jobCompletedTimeoutInMin));  
        Logger.log(listener, "Set job %s constraints with timeout %d minutes.", jobId, jobCompletedTimeoutInMin);    
        
        JobAddParameter param = new JobAddParameter();
        param.withId(jobId)
                .withJobPreparationTask(jobPreparationTask)
                .withPoolInfo(poolInformation)
                .withConstraints(jobConstraints);  

        client.jobOperations().createJob(param);
        
        // We may createTasks with multi-threads if we have many tasks.        
        ArrayList<BatchClientBehavior> listBehaviors = new ArrayList<>();
        listBehaviors.add(new BatchClientParallelOptions(20));
            
        Logger.log(listener, "Job with Id %s is created, now to add tasks...", jobId);        
        client.taskOperations().createTasks(jobId, taskList, listBehaviors);
        Logger.log(listener, "%d tasks are added to job %s.", taskList.size(), jobId);
    }
    
    private static class TaskAddParameterComp implements Comparator<TaskAddParameter>, Serializable { 
        @Override
        public int compare(TaskAddParameter e1, TaskAddParameter e2) {
            if(e1.constraints().maxWallClockTime().getMinutes() < e2.constraints().maxWallClockTime().getMinutes()){
                return 1;
            } else {
                return -1;
            }
        }
    }
    
    private List<TaskAddParameter> createTaskList() throws IOException, URISyntaxException, StorageException, IllegalArgumentException, InterruptedException, InvalidKeyException {
        List<TaskDefinition> taskDefinitionList = jobSplitterHelper.createTaskDefinitionList();
        List<TaskAddParameter> taskList = new ArrayList<>();
        Logger.log(listener, "Preparing task resources for %d tasks...", taskDefinitionList.size());
        for(TaskDefinition td : taskDefinitionList) {
            taskList.add(getTaskAddParameterFromDefinition(td));                
        }
        Logger.log(listener, "Prepared task resources for %d tasks.", taskDefinitionList.size());
        
        LocalResourceEntity scriptsResource = new LocalResourceEntity(scriptTempFolder);
        sharedResourceEntityList.add(scriptsResource);
        
        // Sort taskList to make longer tasks queueing first, so hopefully we may reduce the overall running time.
        // Note: it's not guaranteed tasks queueing first will run first.
        Collections.sort(taskList, new TaskAddParameterComp());
        
        return taskList;
    }
    
    private JobPreparationTask createJobPreparationTask() throws IOException, URISyntaxException, StorageException, InvalidKeyException, IllegalArgumentException, InterruptedException {
        
        addSharedResourcesForJobPreparationTask();
               
        // Upload ResourceEntitys
        String containerName = jobId;

        CloudBlobContainer cloudBlobContainer = AzureStorageHelper.getBlobContainer(listener, storageAccountInfo, containerName, false);
        String sasKey = AzureStorageHelper.getContainerSas(listener, cloudBlobContainer, 3 * 60);
        
        String tempZipFolder = workspaceHelper.getPathRelativeToTempFolder("tempZip");
        if (!Utils.dirExists(tempZipFolder)) {
            Files.createDirectory(Paths.get(tempZipFolder));
        }
     
        Logger.log(listener, "Uploading %d resources to Azure storage...", sharedResourceEntityList.size());
        for (ResourceEntity resource : sharedResourceEntityList) {
            if (resource instanceof LocalResourceEntity){
                ResourceEntityHelper.zipAndUploadLocalResourceEntity(listener, cloudBlobContainer, sasKey, (LocalResourceEntity)resource, tempZipFolder, false);
            }
        }
        Logger.log(listener, "Uploaded %d resources to Azure storage.", sharedResourceEntityList.size());
        
        // Create JobPreparationTask        
        JobPreparationTask jobPreparationTask = new JobPreparationTask();  
        jobPreparationTask.withRunElevated(true);
        
        List<ResourceFile> batchResourceFileList = new ArrayList<>();
        
        for (ResourceEntity resource : sharedResourceEntityList) {
            ResourceFile rfe = new ResourceFile();
            rfe.withFilePath(resource.getBlobName())
                    .withBlobSource(resource.getBlobPath());
            batchResourceFileList.add(rfe);            
        }
        
        jobPreparationTask.withResourceFiles(batchResourceFileList);
        
        // Make sure the command line is path on the VM
        jobPreparationTask.withCommandLine(getJobPreparationTaskCommandLine());

        return jobPreparationTask;
    }
}
