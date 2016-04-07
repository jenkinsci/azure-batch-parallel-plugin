package com.microsoft.azurebatch.jenkins;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.DetailLevel;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.protocol.models.*;
import com.microsoft.windowsazure.storage.StorageException;

import com.microsoft.windowsazure.storage.blob.CloudBlobContainer;
import com.microsoft.windowsazure.storage.blob.CloudBlockBlob;

import com.microsoft.windowsazure.storage.core.Utility;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.*;
import hudson.model.BuildListener;
import hudson.util.CopyOnWriteList;
import hudson.util.EnumConverter;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.tools.ant.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static java.nio.file.StandardOpenOption.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Created by nirajg on 3/15/2016.
 */
public class TestInParallelPostBuild  extends Recorder {

    private final String sevenZExe = "7z.exe";
    private final String sevenZDll = "7z.dll";

    private final String binariesZipped = "binaries.zip";
    private final String compressionToolNameZipped = "7z.zip";
    private final String xUnitZipped = "xUnit.zip";

    private final String testresultFileName = "azbatchtestresult.xml";

    private final String azureBatchTempDirName = "azurebatchtemp";

    private final String ParallelismFileParamName = "Parallelism Definition File";
    private final String BinDropPathElement = "BinDropPath";

    private String compressionToolFilePath;

    //Fields from Jenkins plugin UX
    private String batchAccount;
    private String containerSAS;
    private String filePath;
    private String compressionToolPath;
    private String unitTestBinaryPath;
    private String storageAccount;

    //Fields for internal use
    private String workspacePath;
    private String azureBatchTempDirPath;
    private String definitionFilePath;

    private Map<String, ResourceFileEntity> commonResourceFileEntityMap;
    private Map<String, ResourceFileEntityNonZipped> commonResourceFileEntityNonZippedMap;

    private Map<String, TaskGroupDefinition> taskGroupDefinitionMap;
    private Map<String, ResourceFileEntity> zipAndUploadList;
    private Map<String, ResourceFileEntityNonZipped> uploadList;
    Map<String, String> resourceMapList;

    private List<ResourceFile> resourceFileList;

    private final String placeHolder = "$$FileName$$";

        public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    // They are required for saved Project to retrieve the values
    public String getBatchAccount()
    {
        return batchAccount;
    }

    public String getContainerSAS() {
        return containerSAS;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCompressionToolPath() { return compressionToolPath;}

    public String getUnitTestBinaryPath() { return unitTestBinaryPath;}

    public String getStorageAccount() { return storageAccount;}

    @DataBoundConstructor
    public TestInParallelPostBuild(final String batchAccount,
                                   final String containerSAS,
                                   final String filePath,
                                   final String compressionToolPath,
                                   final String unitTestBinaryPath,
                                   final String storageAccount
                                   ) {
        super();
        this.batchAccount = batchAccount;
        this.containerSAS = containerSAS;
        this.filePath = filePath;
        this.compressionToolPath = compressionToolPath;
        this.unitTestBinaryPath = unitTestBinaryPath;
        this.storageAccount = storageAccount;

        this.compressionToolFilePath = this.compressionToolPath + "\\" + sevenZExe;

        resourceFileList = new ArrayList<ResourceFile>();

    }
    public String getWorkspacePath() {return this.workspacePath;}

    //Test hook
    public String setWorkspacePath() {return this.workspacePath;}


    // Added for testability.
    public void setDefinitionFilePath(String filePath) {this.definitionFilePath = filePath; }

    public TestInParallelPostBuildDescriptor getDescriptor() {
        return (TestInParallelPostBuildDescriptor) super.getDescriptor();
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {

        long startTime = System.currentTimeMillis();
        this.uploadList = new HashMap<String, ResourceFileEntityNonZipped>();
        this.taskGroupDefinitionMap = new HashMap<String, TaskGroupDefinition>();

        this.commonResourceFileEntityMap = new HashMap<String, ResourceFileEntity>();
        this.commonResourceFileEntityNonZippedMap = new HashMap<String, ResourceFileEntityNonZipped>();

        BatchAccountInfo batchAcc = getDescriptor().getBatchAccount(this.batchAccount);
        this.batchAccount = this.batchAccount.trim();

        try {
            String jobId = batchAccount + UUID.randomUUID().toString();
            Utils.print(listener, "JobId" + jobId);
            String compressedFileName = jobId + ".zip";

            //1: Do Pre-req steps
            Utils.print(listener, "1. Executing Prerequisites steps");
            Initialize(build, listener);
            Utils.print(listener, ">Jenkins Workspace Path: " + workspacePath);
            this.definitionFilePath = this.workspacePath + File.separator + this.filePath;

            //2: Validate Paths etc.
            boolean validationResult = Validation(listener);
            if (!validationResult) {
                return validationResult;
            }

            //3. Upload 7z and xUnit tools to Azure Storage
            String zippedxUnitFilePath = this.azureBatchTempDirPath.replace("\"", "") + File.separator + xUnitZipped;
            if(!this.commonResourceFileEntityMap.containsKey(xUnitZipped))
            {
                this.commonResourceFileEntityMap.put(xUnitZipped, new ResourceFileEntity(xUnitZipped, unitTestBinaryPath, zippedxUnitFilePath, "", "" ));
            }

            UploadCommonResourcesAndUpdateMap(build, listener, jobId);
            Upload7ZFilesAndUpdateMap(build, listener, jobId);


            //4. Parse the xml and get the directory
            UpdateTaskGroupDefinitionMap(build, listener);
            Utils.print(listener, "Before updating");
            this.DebugPrintTaskGroupDefinitionMap(listener);
            if (taskGroupDefinitionMap == null | taskGroupDefinitionMap.isEmpty())
            {
                Utils.print(listener, String.format("Skipping tests"));
                return false;
            }

            this.CompressionAndUploadToAzureStorage(build, listener, jobId);
            Utils.print(listener, "After updating");
            this.DebugPrintTaskGroupDefinitionMap(listener);

            StorageAccountInfo storageAcc1 = getDescriptor().getStorageAccount(this.storageAccount);
            String sasKey = AzureStorageHelper.generateSASURL(listener, storageAcc1.getStorageAccName(), storageAcc1.getStorageAccountKey(), jobId, storageAcc1.getBlobEndPointURL());
            Utils.print(listener, String.format("Generated SAS %s for the container", sasKey));

            UpdateSASForResources(build,listener, sasKey);

            //5. Start Creating task + Wait till task finishes
            UseBatchAndSubmitTasks(build, listener, jobId, sasKey);

            //6. Attach ResourceFile as task

            //7. Batch Client Application work (Pool Creation, Job + Submission)

        } catch (BatchErrorException e) {
            listener.getLogger().println("Found exception");
            e.printStackTrace(listener.getLogger());
            listener.getLogger().println(String.format("BatchError %s", e.toString()));
            listener.getLogger().println(String.format("BatchError code = %s, message = %s", e.getBody().getCode(), e.getBody().getMessage()));
            for (BatchErrorDetail detail : e.getBody().getValues()) {
                listener.getLogger().println(String.format("Detail %s=%s", detail.getKey(), detail.getValue()));
            }
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (IOException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (InterruptedException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (URISyntaxException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (StorageException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (Exception e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);

        }
        long timeSpanInMilliSeconds = System.currentTimeMillis() - startTime;

        listener.getLogger().println("Time Elapsed: " + String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeSpanInMilliSeconds),
                TimeUnit.MILLISECONDS.toSeconds(timeSpanInMilliSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpanInMilliSeconds))));

        return true;
    }

    public boolean Validation(BuildListener listener) {
        //Definition File Path
        if (!Utils.fileExists(this.definitionFilePath))
        {
            Utils.print(listener, String.format("%s doesn't exist", this.definitionFilePath));
            return false;
        }
        return true;
    }

    // We can set this value directly, but for unit test we won't have build value, hence this method and null check
    private void SetBuildStatus(AbstractBuild<?, ?> build, Result result) {
        if (build != null) {
            build.setResult(result);
        }
    }
    public boolean UpdateTaskGroupDefinitionMap(AbstractBuild<?, ?> build, BuildListener listener)
    {
        Utils.print(listener, "Starting to create task list");
        try {

            String binDropPathFromXml;
            JAXBContext jaxbContext = JAXBContext.newInstance(TasksDescriptionType.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            TasksDescriptionType t = (TasksDescriptionType) unmarshaller.unmarshal(new File(this.definitionFilePath));
            List<TaskGroupType> taskGroupTypes = t.getTaskGroup();
            if (taskGroupTypes == null | taskGroupTypes.isEmpty()) {
                Utils.print(listener, String.format("%s doesn't contains any TaskGroup", this.definitionFilePath));
                return true;
            }

            for (TaskGroupType taskGroupType : taskGroupTypes) {
                binDropPathFromXml = taskGroupType.binDropPath;
                String absoluteBinDropPath = GetAbsolutePath(binDropPathFromXml);
                Utils.print(listener, "absoluteBinDropPath:" + absoluteBinDropPath);

                //TODO: Do XML validation in prereq and don't proceed if it doesn't go through
                if (binDropPathFromXml == null | binDropPathFromXml.trim().isEmpty()) {
                    Utils.print(listener, String.format("%s doesn't have %s element", ParallelismFileParamName, BinDropPathElement), Utils.TraceType.Error);
                    SetBuildStatus(build, Result.FAILURE);
                    return false;
                }

                if (!Utils.dirExists(absoluteBinDropPath)) {
                    String message = String.format("%s doesn't exist", binDropPathFromXml);
                    Utils.print(listener, message, Utils.TraceType.Error);
                    SetBuildStatus(build, Result.FAILURE);
                    return false;
                }

                TaskGroupDefinition taskGroupDefinition = new TaskGroupDefinition();
                String compressedBinaryName = taskGroupDefinition.TaskGroupGuid + ".zip";
                String compressedBinaryLocalPath = this.azureBatchTempDirPath.replace("\"", "") + File.separator + compressedBinaryName;
                ResourceFileEntity resourceFileEntity = new ResourceFileEntity(compressedBinaryName, absoluteBinDropPath, compressedBinaryLocalPath, "", "");
                if (!taskGroupDefinition.ResourceFileEntityMap.containsKey(resourceFileEntity.name)) {
                    taskGroupDefinition.ResourceFileEntityMap.put(resourceFileEntity.name, resourceFileEntity);
                }

                List<TaskType> taskTypes = taskGroupType.getTask();
                for (TaskType taskType : taskTypes) {
                    taskGroupDefinition.TaskList.add(new TaskDefinition(taskType.getCommand() ,  UUID.randomUUID().toString()));
                }
                if(!taskGroupDefinitionMap.containsKey(taskGroupDefinition.TaskGroupGuid))
                {
                    taskGroupDefinitionMap.put(taskGroupDefinition.TaskGroupGuid, taskGroupDefinition);
                }
            }
        }
        catch(javax.xml.bind.JAXBException e)
        {
            TakeActionFromException(build, listener, e, Result.FAILURE);
            return  false;
        }

        return true;
    }

    public String GetAbsolutePath(String relativePath)
    {
        return this.workspacePath + File.separator + relativePath;
    }

    private void CompressionAndUploadToAzureStorage(AbstractBuild<?, ?> build, BuildListener listener, String containerName)
    {
        Iterator taskDefinationiterator = this.taskGroupDefinitionMap.entrySet().iterator();
        while(taskDefinationiterator.hasNext())
        {
            Map.Entry<String, TaskGroupDefinition> taskGroupDefinitionEntry = (Map.Entry<String, TaskGroupDefinition>)taskDefinationiterator.next();
            TaskGroupDefinition tempTaskGroupDefinition = taskGroupDefinitionEntry.getValue();
            Iterator resourceFileEntityIterator = tempTaskGroupDefinition.ResourceFileEntityMap.entrySet().iterator();

            while(resourceFileEntityIterator.hasNext())
            {
                Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>)resourceFileEntityIterator.next();
                ResourceFileEntity tempRFE = resourceFileEntityEntry.getValue();
                ZipIt(build, listener, tempRFE.zippedFilePath , tempRFE.unZippedfilePath);
                URI uploadedBlobUri = UploadToAzureStorage(build, listener, containerName, tempRFE.name, tempRFE.zippedFilePath);
                tempRFE.blobPath = uploadedBlobUri.toString();
                tempTaskGroupDefinition.ResourceFileEntityMap.put(resourceFileEntityEntry.getKey(),tempRFE);
                taskGroupDefinitionMap.put(taskGroupDefinitionEntry.getKey(),tempTaskGroupDefinition );
            }
        }
    }

    private void DebugPrintTaskGroupDefinitionMap(BuildListener listener) {
        Iterator taskDefinationiterator = this.taskGroupDefinitionMap.entrySet().iterator();
        while (taskDefinationiterator.hasNext()) {
            Map.Entry<String, TaskGroupDefinition> taskGroupDefinitionEntry = (Map.Entry<String, TaskGroupDefinition>) taskDefinationiterator.next();
            TaskGroupDefinition tempTaskGroupDefinition = taskGroupDefinitionEntry.getValue();
            Utils.print(listener, "TaskGroupGuid: " + tempTaskGroupDefinition.TaskGroupGuid);
            for(TaskDefinition taskDefinition : tempTaskGroupDefinition.TaskList) {
                Utils.print(listener, "TaskCommand: " + taskDefinition.TaskCommand);
                Utils.print(listener, "TaskId: " + taskDefinition.TaskId);
            }
            Iterator resourceFileEntityIterator = tempTaskGroupDefinition.ResourceFileEntityMap.entrySet().iterator();

            while (resourceFileEntityIterator.hasNext()) {
                Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>) resourceFileEntityIterator.next();
                ResourceFileEntity tempRFE = resourceFileEntityEntry.getValue();
                Utils.print(listener, "name: " + tempRFE.name);
                Utils.print(listener, "unZippedfilePath: " + tempRFE.unZippedfilePath);
                Utils.print(listener, "zippedFilePath: " + tempRFE.zippedFilePath);
                Utils.print(listener, "bloburl: " + tempRFE.blobPath);
                Utils.print(listener, "blobSAS: " + tempRFE.blobSAS);
            }

        }
    }

    private void DebugPrintCommonResourceFileEntityZippedMap(BuildListener listener) {
        Iterator iterator = this.commonResourceFileEntityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>) iterator.next();
            ResourceFileEntity tempResourceFileEntity = resourceFileEntityEntry.getValue();
            Utils.print(listener, "zip, name:" + tempResourceFileEntity.name);
            Utils.print(listener, "zip, filePath:" + tempResourceFileEntity.zippedFilePath);
            Utils.print(listener, "zip, filePath:" + tempResourceFileEntity.unZippedfilePath);
            Utils.print(listener, "zip, blobPath:" + tempResourceFileEntity.blobPath);
            Utils.print(listener, "zip, blobSAS:" + tempResourceFileEntity.blobSAS);
        }
    }

    private void DebugPrintCommonResourceFileEntityNonZippedMap(BuildListener listener) {
        Iterator iterator = this.commonResourceFileEntityNonZippedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntityNonZipped> resourceFileEntityNonZippedEntry = (Map.Entry<String, ResourceFileEntityNonZipped>) iterator.next();
            ResourceFileEntityNonZipped tempResourceFileEntityNonZipped = resourceFileEntityNonZippedEntry.getValue();
            Utils.print(listener, "nonzip, name:" + tempResourceFileEntityNonZipped.name);
            Utils.print(listener, "nonzip, filePath:" + tempResourceFileEntityNonZipped.filePath);
            Utils.print(listener, "nonzip, blobPath:" + tempResourceFileEntityNonZipped.blobPath);
            Utils.print(listener, "nonzip, blobSAS:" + tempResourceFileEntityNonZipped.blobSAS);
        }
    }


    private void UseBatchAndSubmitTasks(AbstractBuild<?, ?> build, final BuildListener listener, String jobId, String sasKey)  {

        BatchAccountInfo batchAcc = getDescriptor().getBatchAccount(this.batchAccount);

        Utils.print(listener, "2. Connecting to Azure Batch and executing tests in parallel..");
        listener.getLogger().println(">AzureBatchAccount, " + batchAcc.getBatchAccountName() + "!");
        listener.getLogger().println(">AzureBatchUrl, " + batchAcc.getBatchServiceURL() + "!");
        BatchSharedKeyCredentials cred = new BatchSharedKeyCredentials(batchAcc.getBatchServiceURL(), batchAcc.getBatchAccountName(), batchAcc.getBatchAccountKey());

        BatchClient client = BatchClient.Open(cred);

        try {
            String userName = System.getProperty("user.name");
            String poolId = batchAccount + "-jenkinspool";
            listener.getLogger().println("PoolName: " + poolId);

            listener.getLogger().println("Creating Azure Batch Pool of VMs...");
            CloudPool sharedPool = CreatePool(build, client, poolId, listener);

            PoolInformation poolInfo = new PoolInformation();
            poolInfo.setPoolId(sharedPool.getId());
            listener.getLogger().println(String.format("Pool with id: %s got created", sharedPool.getId()));
            listener.getLogger().println("Creating Azure Batch Job with JobId:" + jobId);

            client.getJobOperations().createJob(jobId, poolInfo);
            listener.getLogger().println(String.format("Job with Id: %s is created", jobId));

            CloudJob boundJob = client.getJobOperations().getJob(jobId);

            List<TaskAddParameter> taskList = new ArrayList<TaskAddParameter>();

            //////////////////// Starting New way of adding tasks ////////////////
            for (TaskGroupDefinition taskGroupDefinition : this.taskGroupDefinitionMap.values()) {
                    for(TaskDefinition taskDefinition : taskGroupDefinition.TaskList) {
                        taskList.add(GetTaskOnePerTaskType(build, listener, jobId, sasKey, taskDefinition, taskGroupDefinition.ResourceFileEntityMap));
                    }
            }
            //////////////////// Ending New way of adding tasks ////////////////

            Utils.print(listener, "Submitting task(s) to Azure Batch to run in Parallel");

            for(TaskAddParameter task : taskList) {
                client.getTaskOperations().createTask(boundJob.getId(), task);
            }

            // wait for the task to complete
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            boolean steady = false;
            int timeout = 20 * 60 * 1000;
            while (elapsedTime < timeout) {
                List<CloudTask> taskCollection = client.getTaskOperations().listTasks(boundJob.getId());

                boolean allComplete = true;
                for (CloudTask task : taskCollection) {
                    //listener.getLogger().println("Task Detail:" + task.getId());
                    if (task.getState() != TaskState.COMPLETED) {
                        allComplete = false;
                        break;
                    }
                }
                if (allComplete) {
                    steady = true;
                    break;
                }

                listener.getLogger().println("Waiting for Azure Batch tasks to complete...");
                Thread.sleep(30 * 1000);
                elapsedTime = (new Date()).getTime() - startTime;
            }

            if (!steady) {
                listener.getLogger().println("TSM timed out.");
                build.setResult(Result.FAILURE);
                build.setDescription(String.format("Failed due to timed out, current timeout value is set to %d minutes", (timeout/1000)/60));
            }
            Utils.print(listener, "All Tasks are completed.. starting to summarize the results");
            Utils.print(listener, "$$Result:" + build.getResult());
            int taskCount = 1;
            for(TaskAddParameter task: taskList) {
                Utils.print(listener, "Fetching test results for task: " + task.getId());
                try {
                    CloudTask boundTask = client.getTaskOperations().getTask(boundJob.getId(), task.getId());
                    Utils.print(listener, "$$Result:" + build.getResult());
                    Utils.print(listener, String.format("------- Task %s result detail ----- ", task.getId()));
                    String filePrefix = azureBatchTempDirPath + File.separator + task.getId();
                    String StandardOutFileName = "stdout.txt";
                    String theString;
                    InputStream stream = client.getFileOperations().getFileFromTask(boundJob.getId(), boundTask.getId(), StandardOutFileName);

                    Charset encoding = Charset.defaultCharset();
                    //theString = IOUtils.toString(stream, encoding);
                    //Utils.print(listener, theString, Utils.TraceType.Debug);
                    copyInputStreamToFile(build, listener, stream,  new File(filePrefix + "_stdout.txt"));

                    //FileProperties property = client.getFileOperations().getFilePropertiesFromTask(boundJob.getId(), boundTask.getId(), StandardOutFileName);
                    //Utils.print(listener, String.format("File Name: %s size: %s ", StandardOutFileName, Long.toString(property.getContentLength())));

                    String testResultFileName = testresultFileName;
                    stream = client.getFileOperations().getFileFromTask(boundJob.getId(), boundTask.getId(), testResultFileName);
                    OutputStream outputStream = new FileOutputStream(filePrefix + "_testresult.xml");
                    //theString = IOUtils.toString(stream, encoding);
                    //Utils.print(listener, theString);
                    IOUtils.copy(stream, outputStream);
                    //property = client.getFileOperations().getFilePropertiesFromTask(boundJob.getId(), boundTask.getId(), testResultFileName);
                    //Utils.print(listener, String.format("File Name: %s size: %s ", testResultFileName, Long.toString(property.getContentLength())));
                    //copyInputStreamToFile(build, listener, stream,  new File(filePrefix + "_stdout.txt"));


                    String StandardErrorFileName = "stderr.txt";
                    stream = client.getFileOperations().getFileFromTask(boundJob.getId(), boundTask.getId(), StandardErrorFileName);
                    outputStream = new FileOutputStream(filePrefix + "_stderr.txt");
                    IOUtils.copy(stream, outputStream);
                    //theString = IOUtils.toString(stream, encoding);
                    //Utils.print(listener, theString);
                    // property = client.getFileOperations().getFilePropertiesFromTask(boundJob.getId(), boundTask.getId(), StandardErrorFileName);
                    //Utils.print(listener, String.format("File Name: %s size: %s ", StandardErrorFileName, Long.toString(property.getContentLength())));

                    Utils.print(listener, String.format("End: Task %s result detail ----- ", task.getId()));
                } catch (BatchErrorException e) {
                    listener.getLogger().println(String.format("Found exception while retrieving the task %s detail ", task.getId()));
                    e.printStackTrace(listener.getLogger());
                    listener.getLogger().println(String.format("BatchError %s", e.toString()));
                    if (e.getBody() != null & e.getBody().getCode() != null & e.getBody().getMessage() != null) {
                        listener.getLogger().println(String.format("BatchError code = %s, message = %s", e.getBody().getCode(), e.getBody().getMessage()));
                        if (e.getBody().getValues() != null) {
                            for (BatchErrorDetail detail : e.getBody().getValues()) {
                                listener.getLogger().println(String.format("Detail %s=%s", detail.getKey(), detail.getValue()));
                            }
                        }

                    }
                } catch (IOException e) {
                    listener.getLogger().println(String.format("Found exception while retrieving the task %s detail ", task.getId()));
                    Utils.print(listener, e.getMessage());
                }
                taskCount++;

            }

            Utils.print(listener, "$$Result:" + build.getResult());
            Utils.print(listener, String.format("Finished Tasks (Total %d) execution - see summary for pass/fail details", --taskCount));

        } catch (BatchErrorException e) {
            listener.getLogger().println("Found exception");
            e.printStackTrace(listener.getLogger());
            listener.getLogger().println(String.format("BatchError %s", e.toString()));
            listener.getLogger().println(String.format("BatchError code = %s, message = %s", e.getBody().getCode(), e.getBody().getMessage()));
            for (BatchErrorDetail detail : e.getBody().getValues()) {
                listener.getLogger().println(String.format("Detail %s=%s", detail.getKey(), detail.getValue()));
            }
        } catch (IOException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (InterruptedException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } finally {
            try {
                listener.getLogger().println("Deleting job with id : " + jobId);
                client.getJobOperations().deleteJob(jobId);
                listener.getLogger().println("Deleted Job with id : " + jobId);
            } catch (BatchErrorException e) {
                TakeActionFromException(build, listener, e, Result.FAILURE);
            } catch (IOException e) {
                TakeActionFromException(build, listener, e, Result.FAILURE);
            }
        }

    }

    private void copyInputStreamToFile(AbstractBuild<?, ?> build, final BuildListener listener, InputStream in, File file ) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
           TakeActionFromException(build, listener, e, Result.FAILURE);
        }
    }

    private TaskAddParameter GetTaskOnePerTaskType(AbstractBuild<?, ?> build, BuildListener listener, String jobId, String sasKey, TaskDefinition taskDefinition , Map<String, ResourceFileEntity> taskResourceFileMap) {

        try {
            String taskId = taskDefinition.TaskId;

            TaskAddParameter taskAddParam = new TaskAddParameter();
            taskAddParam.setId(taskId);

            List<String> commandLineList = new ArrayList<String>();

            String binaryDirOnTVM = "aztaskbinaries";

            commandLineList.add(String.format("md %s", binaryDirOnTVM));
            commandLineList.add(String.format("pushd %s", binaryDirOnTVM));
            commandLineList.add(String.format("..\\%s x -aoa ..\\%s", sevenZExe, binariesZipped));
            commandLineList.add("popd");

            commandLineList.add("md xunitbin");
            commandLineList.add("pushd xunitbin");
            commandLineList.add(String.format("..\\%s x -aoa ..\\%s", sevenZExe, xUnitZipped));
            commandLineList.add("popd");

            String xUnit = "xunit.console.exe";
            String testCode = taskDefinition.TaskCommand;

            commandLineList.add(String.format("pushd %s", binaryDirOnTVM));
            commandLineList.add(String.format("..\\xunitbin\\%s %s -xml %s", xUnit, testCode, testresultFileName));
            commandLineList.add(String.format("copy %s ..\\..\\", testresultFileName));
            commandLineList.add("popd");

            //commandLineList.add(String.format("PING 1.1.1.1 -n 1 -w 120000"));
            String taskCmdFile = taskId + ".cmd";
            String workPathCmd = this.azureBatchTempDirPath.replace("\"", "") + File.separator + taskCmdFile;
            Path p = Paths.get(workPathCmd);
            if (Files.exists(p)) {
                Files.delete(p);
            }

            // Generate the task command line at run time based on the dll
            try (FileWriter writer = new FileWriter(new File(workPathCmd), false)) {
                for (String line : commandLineList) {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                }
            } catch (IOException x) {
                Utils.print(listener, "Task command file generation failed");
                Utils.print(listener, x.getMessage());

            }

            List<ResourceFile> localResourceFileList = new ArrayList<ResourceFile>();
            URI taskCmd = UploadToAzureStorage(build, listener, jobId, taskId, workPathCmd);
            String sasTaskCmd = taskCmd.toString() + "?" + sasKey;
            ResourceFile rf = new ResourceFile();
            rf.setBlobSource(sasTaskCmd);
            rf.setFilePath(taskCmdFile);
            localResourceFileList.add(rf);

            for (ResourceFileEntity r : taskResourceFileMap.values()) {
                ResourceFile rfe = new ResourceFile();
                rfe.setFilePath(binariesZipped);
                rfe.setBlobSource(r.blobSAS);
                localResourceFileList.add(rfe);
            }

            for (ResourceFileEntity r : commonResourceFileEntityMap.values()) {
                ResourceFile rfe = new ResourceFile();
                rfe.setFilePath(r.name);
                rfe.setBlobSource(r.blobSAS);
                localResourceFileList.add(rfe);
            }

            for (ResourceFileEntityNonZipped r : commonResourceFileEntityNonZippedMap.values()) {
                ResourceFile rfe = new ResourceFile();
                rfe.setFilePath(r.name);
                rfe.setBlobSource(r.blobSAS);
                localResourceFileList.add(rfe);
            }

            taskAddParam.setResourceFiles(localResourceFileList);

            Utils.print(listener, String.format("------- Created a task %s for %s -- Resource Files -------", taskId, testCode), Utils.TraceType.Debug);
            int i = 1;
            for (ResourceFile file : localResourceFileList) {
                Utils.print(listener, " (" + i + ") FilePath:" + file.getFilePath());
                Utils.print(listener, " (" + i + ") ResourcePath:)" + file.getBlobSource());
                i++;
            }

            String cmdLine = taskCmdFile;
            taskAddParam.setCommandLine(cmdLine);

            return taskAddParam;
        } catch (IOException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
        /*
        catch(JAXBException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
*/
        return null;
    }

    private TaskAddParameter GetTasksOnePerDll(AbstractBuild<?, ?> build, BuildListener listener, File dllFile, String jobId, String sasKey){

        try {
            String taskId = UUID.randomUUID().toString();
            TaskAddParameter taskAddParam = new TaskAddParameter();
            taskAddParam.setId(taskId);

            List<String> commandLineList = new ArrayList<String>();

            String binaryDirOnTVM = "aztaskbinaries";

            commandLineList.add(String.format("md %s", binaryDirOnTVM));
            commandLineList.add(String.format("pushd %s", binaryDirOnTVM));
            commandLineList.add(String.format("..\\%s x -aoa ..\\%s", sevenZExe, binariesZipped));
            commandLineList.add("popd");

            commandLineList.add("md xunitbin");
            commandLineList.add("pushd xunitbin");
            commandLineList.add(String.format("..\\%s x -aoa ..\\%s", sevenZExe, xUnitZipped));
            commandLineList.add("popd");

            String xUnit = "xunit.console.exe";
            String testCode = dllFile.getName();

            commandLineList.add(String.format("pushd %s", binaryDirOnTVM));
            commandLineList.add(String.format("..\\xunitbin\\%s %s -xml %s", xUnit, testCode, testresultFileName));
            commandLineList.add(String.format("copy %s ..\\..\\", testresultFileName));
            commandLineList.add("popd");

            //commandLineList.add(String.format("PING 1.1.1.1 -n 1 -w 120000"));
            String taskCmdFile = taskId + ".cmd";
            String workPathCmd = this.azureBatchTempDirPath.replace("\"", "") + File.separator + taskCmdFile;
            Path p = Paths.get(workPathCmd);
            if (Files.exists(p)) {
                Files.delete(p);
            }

            // Generate the task command line at run time based on the dll
            try (FileWriter writer = new FileWriter(new File(workPathCmd), false)) {
                for (String line : commandLineList) {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                }
            } catch (IOException x) {
                Utils.print(listener, "Task command file generation failed");
                Utils.print(listener, x.getMessage());

            }

            URI taskCmd = UploadToAzureStorage(build, listener, jobId, taskId, workPathCmd);
            String sasTaskCmd = taskCmd.toString() + "?" + sasKey;
            ResourceFile rf = new ResourceFile();
            rf.setBlobSource(sasTaskCmd);
            rf.setFilePath(taskCmdFile);

            List<ResourceFile> localResourceFileList = new ArrayList<ResourceFile>();
            localResourceFileList.clear();
            Map<String, String> localResourceMapList = new HashMap<String, String>();
            localResourceMapList.clear();

            // TODO: needs to be revisited, do we still need a global map list?
            if (!resourceMapList.containsKey(taskCmdFile)) {
                resourceMapList.put(taskCmdFile, taskCmdFile);
                localResourceFileList.add(rf);
            }

            // resourceFileList contains common ResourceFiles across all tasks
            for(ResourceFile r : resourceFileList)
            {
                String key = r.getFilePath();
                Utils.print(listener, "FilePath is" + r.getFilePath());
                if(!localResourceMapList.containsKey(key))
                {
                    localResourceMapList.put(key, key);
                    Utils.print(listener, "Adding Resource to task, BlobSource:" + r.getBlobSource());
                    Utils.print(listener, "Adding Resource to task, FilePath:" + r.getFilePath());
                    localResourceFileList.add(r);
                }
            }
/*
            for(ResourceFile r : uploadList)
            {
                String key = r.getFilePath();
                if(!localResourceMapList.containsKey(key))
                {
                    localResourceMapList.put(key, key);
                    Utils.print(listener, "Adding Resource to task, BlobSource:" + r.getBlobSource());
                    Utils.print(listener, "Adding Resource to task, FilePath:" + r.getFilePath());
                    localResourceFileList.add(r);
                }
            }
*/
            taskAddParam.setResourceFiles(localResourceFileList);

            Utils.print(listener, String.format("------- Created a task %s for %s -- Resource Files -------", taskId, testCode), Utils.TraceType.Debug);
            int i = 1;
            for (ResourceFile file : localResourceFileList) {
                Utils.print(listener, " (" + i  +") FilePath:" + file.getFilePath());
                Utils.print(listener, " (" + i  +") ResourcePath:)" + file.getBlobSource());
                i++;
            }

            String cmdLine = taskCmdFile;
            taskAddParam.setCommandLine(cmdLine);

            return taskAddParam;
        } catch(IOException e)
        {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
        /*
        catch(JAXBException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
*/
        return null;
    }

    private void UploadCommonResourcesAndUpdateMap(AbstractBuild<?, ?> build, BuildListener listener, String containerName) {

        Utils.print(listener, "Before nonzip upload");
        this.DebugPrintCommonResourceFileEntityZippedMap(listener);

        Iterator iterator = this.commonResourceFileEntityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>) iterator.next();
            ResourceFileEntity tempResourceFileEntity = resourceFileEntityEntry.getValue();
            ZipIt(build, listener, tempResourceFileEntity.zippedFilePath , tempResourceFileEntity.unZippedfilePath);
            tempResourceFileEntity.blobPath = UploadToAzureStorage(build, listener, containerName, tempResourceFileEntity.name, tempResourceFileEntity.zippedFilePath).toString();
            commonResourceFileEntityMap.put(tempResourceFileEntity.name, tempResourceFileEntity);
        }

        Utils.print(listener, "After nonzip upload");
        this.DebugPrintCommonResourceFileEntityZippedMap(listener);
    }

    //TODO: Check if the 7z DLL and Exe exists in prereq section
    private void Upload7ZFilesAndUpdateMap(AbstractBuild<?, ?> build, BuildListener listener, String containerName) {
        this.commonResourceFileEntityNonZippedMap.put(sevenZDll, new ResourceFileEntityNonZipped(sevenZDll, compressionToolPath + File.separator + sevenZDll, "", ""));
        this.commonResourceFileEntityNonZippedMap.put(sevenZExe, new ResourceFileEntityNonZipped(sevenZExe, compressionToolPath + File.separator + sevenZExe, "", ""));

        Utils.print(listener, "Before nonzip upload");
        this.DebugPrintCommonResourceFileEntityNonZippedMap(listener);

        File sevenZExePath = new File(commonResourceFileEntityNonZippedMap.get(sevenZExe).filePath);
        File sevenZdllPath = new File(commonResourceFileEntityNonZippedMap.get(sevenZDll).filePath);

        Iterator iterator = this.commonResourceFileEntityNonZippedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntityNonZipped> resourceFileEntityNonZippedEntry = (Map.Entry<String, ResourceFileEntityNonZipped>) iterator.next();
            ResourceFileEntityNonZipped tempResourceFileEntityNonZipped = resourceFileEntityNonZippedEntry.getValue();
            tempResourceFileEntityNonZipped.blobPath = UploadToAzureStorage(build, listener, containerName, tempResourceFileEntityNonZipped.name, tempResourceFileEntityNonZipped.filePath).toString();

            commonResourceFileEntityNonZippedMap.put(tempResourceFileEntityNonZipped.name, tempResourceFileEntityNonZipped);
        }

        Utils.print(listener, "After nonzip upload");
        this.DebugPrintCommonResourceFileEntityNonZippedMap(listener);
    }

    private void Upload7ZFiles(AbstractBuild<?, ?> build, BuildListener listener, String containerName)
    {
        this.uploadList.put(sevenZDll, new ResourceFileEntityNonZipped(sevenZDll,compressionToolPath + File.separator + sevenZDll, "","" ));
        this.uploadList.put(sevenZExe, new ResourceFileEntityNonZipped(sevenZExe,compressionToolPath + File.separator + sevenZExe, "","" ));
        File sevenZExePath = new File(uploadList.get(sevenZExe).filePath);
        File sevenZdllPath = new File(uploadList.get(sevenZDll).filePath);

        for(String key: uploadList.keySet()) {
            ResourceFileEntityNonZipped r = uploadList.get(key);
            String path = r.filePath;
            File tempFile = new File(path);
            if(!tempFile.exists())
            {
                Utils.print(listener, path + " is expected, but not present");
                // TODO: Fail the build
            }

            r.blobPath = UploadToAzureStorage(build, listener,containerName,r.name,path).toString();

            uploadList.put(key, r);
        }
    }

    private void UpdateSAS(AbstractBuild<?, ?> build, BuildListener listener, String sasKey)
    {
        for(String key: zipAndUploadList.keySet())
        {
            ResourceFileEntity e = zipAndUploadList.get(key);
            e.blobSAS = e.blobPath +  "?" + sasKey;
            zipAndUploadList.put(key, e);
        }

        for(String key: uploadList.keySet())
        {
            ResourceFileEntityNonZipped r = uploadList.get(key);
            r.blobSAS = r.blobPath + "?" + sasKey;
            uploadList.put(key, r);
        }
    }

    private void UpdateSASForResources(AbstractBuild<?, ?> build, BuildListener listener, String sasKey)
    {
        // 1
        Utils.print(listener, "Before nonzip SAS");
        this.DebugPrintCommonResourceFileEntityZippedMap(listener);

        Iterator iterator = this.commonResourceFileEntityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>) iterator.next();
            ResourceFileEntity tempResourceFileEntity = resourceFileEntityEntry.getValue();
            tempResourceFileEntity.blobSAS = tempResourceFileEntity.blobPath + "?" + sasKey;
            commonResourceFileEntityMap.put(tempResourceFileEntity.name, tempResourceFileEntity);
        }

        Utils.print(listener, "After nonzip SAS");
        this.DebugPrintCommonResourceFileEntityZippedMap(listener);

        // 2
        Utils.print(listener, "Before nonzip SAS");
        this.DebugPrintCommonResourceFileEntityNonZippedMap(listener);

        iterator = this.commonResourceFileEntityNonZippedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResourceFileEntityNonZipped> resourceFileEntityNonZippedEntry = (Map.Entry<String, ResourceFileEntityNonZipped>) iterator.next();
            ResourceFileEntityNonZipped tempResourceFileEntityNonZipped = resourceFileEntityNonZippedEntry.getValue();
            tempResourceFileEntityNonZipped.blobSAS = tempResourceFileEntityNonZipped.blobPath + "?" + sasKey;
            commonResourceFileEntityNonZippedMap.put(tempResourceFileEntityNonZipped.name, tempResourceFileEntityNonZipped);
        }

        Utils.print(listener, "After nonzip SAS");
        this.DebugPrintCommonResourceFileEntityNonZippedMap(listener);

        // 3
        Utils.print(listener, "Before updating SAS");
        this.DebugPrintTaskGroupDefinitionMap(listener);

        Iterator taskDefinationiterator = this.taskGroupDefinitionMap.entrySet().iterator();
        while(taskDefinationiterator.hasNext())
        {
            Map.Entry<String, TaskGroupDefinition> taskGroupDefinitionEntry = (Map.Entry<String, TaskGroupDefinition>)taskDefinationiterator.next();
            TaskGroupDefinition tempTaskGroupDefinition = taskGroupDefinitionEntry.getValue();
            Iterator resourceFileEntityIterator = tempTaskGroupDefinition.ResourceFileEntityMap.entrySet().iterator();

            while(resourceFileEntityIterator.hasNext())
            {
                Map.Entry<String, ResourceFileEntity> resourceFileEntityEntry = (Map.Entry<String, ResourceFileEntity>)resourceFileEntityIterator.next();
                ResourceFileEntity tempRFE = resourceFileEntityEntry.getValue();
                tempRFE.blobSAS = tempRFE.blobPath + "?" + sasKey;
                tempTaskGroupDefinition.ResourceFileEntityMap.put(resourceFileEntityEntry.getKey(),tempRFE);
                taskGroupDefinitionMap.put(taskGroupDefinitionEntry.getKey(),tempTaskGroupDefinition );
            }
        }

        Utils.print(listener, "Afer updating SAS");
        this.DebugPrintTaskGroupDefinitionMap(listener);
    }

    private void CompressionAndUploadToAzure(AbstractBuild<?, ?> build, BuildListener listener, String containerName)
    {
        for(String key : zipAndUploadList.keySet())
        {
            ResourceFileEntity e = zipAndUploadList.get(key);
            ZipIt(build, listener, e.zippedFilePath,  e.unZippedfilePath);
            URI uploadedBlobUri = UploadToAzureStorage(build, listener, containerName, e.name , e.zippedFilePath);
            e.blobPath = uploadedBlobUri.toString();
            zipAndUploadList.put(key, e);
        }

        /*
        ZipIt(listener, compressedFilePath,  binDropFilePath);
        ZipIt(listener, compressionToolCompressed,  compressionToolPath);
        */
    }

    private URI UploadToAzureStorage(AbstractBuild<?, ?> build, BuildListener listener, String containerName, String blobName, String localFilePath) {
        try {
            Utils.print(listener, String.format("Uploding the compressed file to Azure Storage, StorageAccount: %s ContainerName: %s BlobName: %s", this.storageAccount, containerName, blobName));
            StorageAccountInfo storageAcc = getDescriptor().getStorageAccount(this.storageAccount);
            CloudBlobContainer cloudBlobContainer = AzureStorageHelper.getBlobContainerReference( storageAcc.getStorageAccName(), storageAcc.getStorageAccountKey(), storageAcc.getBlobEndPointURL(), containerName, true, true, false, listener);
            Utils.print(listener, String.format("Got the blob ref"));
            CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(blobName);
            URI uploadedBlobUri = AzureStorageHelper.upload(listener, blob, new FilePath(new File(localFilePath)));
            Utils.print(listener, String.format("Done: Uploding the compressed file to Azure Storage"));
            return uploadedBlobUri;
        } catch (IOException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (InterruptedException e) {
            Utils.print(listener, e.getMessage());
        } catch (URISyntaxException e) {
            Utils.print(listener, e.getMessage());
        } catch (StorageException e) {
            Utils.print(listener, e.getMessage());
        }
        Utils.print(listener, String.format("Reached unwanted code"));
        return null;
    }

    private void Initialize(AbstractBuild<?, ?> build, BuildListener listener) {

        //TODO: make sure that xUnit and 7z path exists

        // Do:
        // 1. Get Workspace path
        // 2. Create a temporary directory where Batch will store compressed data
        // 3.
        try {

            Map<String, String> envVars = build.getEnvironment(listener);
            this.workspacePath = envVars.get("WORKSPACE");
            this.azureBatchTempDirPath = workspacePath + "\\" + this.azureBatchTempDirName;

            // Create the directory
            File azureBatchTempDir = new File(azureBatchTempDirPath);

            Utils.print(listener, String.format("Creating directory to hold compressed files:", azureBatchTempDir.toString()));

            // if the directory does not exist, create it else detlete it
            if (azureBatchTempDir.exists()) {
                Utils.print(listener, azureBatchTempDirPath + " already exist.. deleting it");
               Utils.deleteDirectoryIncludeContent(azureBatchTempDir);
                if (!azureBatchTempDir.exists()) {
                    Utils.print(listener, azureBatchTempDirPath + " deletion worked");
                }
            }

            Utils.print(listener, "Creating directory: " + azureBatchTempDirPath);
            boolean created = false;

            try {
                azureBatchTempDir.mkdir();
                created = true;
            } catch (SecurityException e) {
                Utils.print(listener, "Failed to create directory: " + azureBatchTempDirPath);
                Utils.print(listener, e.getMessage());
            }

            if (azureBatchTempDir.exists()) {
                Utils.print(listener, azureBatchTempDirPath + " got created");
            }
            else
            {
                throw new FileNotFoundException(String.format("Directory : %s doesn't exist",azureBatchTempDirPath));
            }

        } catch (InterruptedException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        } catch (IOException e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
    }

    void TakeActionFromException(AbstractBuild<?, ?> build, BuildListener listener, Exception e, Result r)
    {
        Utils.print(listener, "Found Exception:", Utils.TraceType.Error);
        Utils.print(listener, "Error: " + e.toString());
        Utils.print(listener,"Message: " + e.getMessage(), Utils.TraceType.Error);
        for(StackTraceElement s:e.getStackTrace())
        {
            Utils.print(listener, "     " + s.toString());
        }

        if(build != null) {
            build.setResult(r);
        }
    }

    private void ZipIt(AbstractBuild<?, ?> build, BuildListener listener, String targetZipFilePath, String inputFilePath ) {

        //TODO: current limitation is it flatten out directory structure i.e. no directory hierarchy
        // See the answer commented in the function below
        Utils.print(listener, "Compressing binaries...");

        List<String> fileList = new ArrayList<String>();

        Utils.print(listener, "Collecting files under: " + inputFilePath);

        fileList = Traverse(build, listener, new File(inputFilePath));
        Utils.print(listener, "Finished collecting files under: " + inputFilePath);

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
            Utils.print(listener, "Target compressed file: " + targetZipFilePath);
            fos = new FileOutputStream(targetZipFilePath);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            for (String filePath : fileList) {
                File input = new File(filePath);
                fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                //Utils.print(listener, "Adding: " + input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = fis.read(tmp)) != -1) {
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
            }
            zipOut.close();
            Utils.print(listener, "Done... Created: " + targetZipFilePath);

        }
        //TODO: mark the build failure
        catch (FileNotFoundException e) {
            Utils.print(listener, e.getMessage());
        } catch (IOException e) {
            Utils.print(listener, e.getMessage());
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (Exception ex) {
                Utils.print(listener, ex.getMessage());
            }
        }

        /*
        Found here : http://stackoverflow.com/questions/1399126/java-util-zip-recreating-directory-structure
        If you don't want to bother dealing with byte input streams, buffer sizes, and other low level details. You can use Ant's Zip libraries from your java code (maven dependencies can be found here). Here's now I make a zip consisting a list of files & directories:

        public static void createZip(File zipFile, List<String> fileList) {

            Project project = new Project();
            project.init();

            Zip zip = new Zip();
            zip.setDestFile(zipFile);
            zip.setProject(project);

            for(String relativePath : fileList) {

                //noramalize the path (using commons-io, might want to null-check)
                String normalizedPath = FilenameUtils.normalize(relativePath);

                //create the file that will be used
                File fileToZip = new File(normalizedPath);
                if(fileToZip.isDirectory()) {
                    ZipFileSet fileSet = new ZipFileSet();
                    fileSet.setDir(fileToZip);
                    fileSet.setPrefix(fileToZip.getPath());
                    zip.addFileset(fileSet);
                } else {
                    FileSet fileSet = new FileSet();
                    fileSet.setDir(new File("."));
                    fileSet.setIncludes(normalizedPath);
                    zip.addFileset(fileSet);
                }
            }

            Target target = new Target();
            target.setName("ziptarget");
            target.addTask(zip);
            project.addTarget(target);
            project.executeTarget("ziptarget");
        }
        */
    }


    private void UpdateResourceFileList(AbstractBuild<?, ?> build, String userSpecifiedDir, String localPath, BuildListener listener, String containerSASWithPlaceHolder) {
        Utils.print(listener, "Starting to retrieve the files");
        if(this.resourceFileList == null)
        {
            resourceFileList = new ArrayList<ResourceFile>();
        }

        TraverseAndUpdateResouceFileList(build, userSpecifiedDir, new File(localPath), localPath, listener, containerSASWithPlaceHolder);
        Utils.print(listener, "Done retrieving the files");
        Utils.print(listener,"First blob source" + resourceFileList.get(0).getBlobSource());
        Utils.print(listener,"Total ResourceFiles" + resourceFileList.size());
    }

    private List<String> Traverse(AbstractBuild<?, ?> build, BuildListener listener, File inputDir)
    {
        List<String> fileList= new ArrayList<String>();
        List<String> tempFileList= new ArrayList<String>();
        try {
            //Utils.print(listener, inputDir.getCanonicalPath());
            File[] files = inputDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    Utils.print(listener, "Directory: " + file.getCanonicalPath(), Utils.TraceType.Debug);
                    //TODO; we need to also upload the dll from the sub directory
                    //TODO: uncomment the code below and also enable other zipping options
                    /*
                    tempFileList = Traverse(listener, file);
                    if(tempFileList != null & tempFileList.size() > 0) {
                        for (String aFile : tempFileList) {
                            fileList.add(aFile);
                        }
                    }*/
                } else {
                    //Utils.print(listener, "File: " + file.getCanonicalPath(), Utils.TraceType.Debug);
                    fileList.add(file.getCanonicalPath());
                }
            }
            return fileList;
        }catch(Exception e) {
            Utils.print(listener, "Failed to collect files under: " + inputDir);
            Utils.print(listener, e.getMessage());
        }
        return null;
    }

    private void TraverseAndUpdateResouceFileList(AbstractBuild<?, ?> build, String userSpecifiedDir,File dir, String pathBase, BuildListener listener, String containerSASWithPlaceHolder) {
        try {
            File[] files = dir.listFiles();
            for(File file : files)
            {
                if(file.isDirectory())
                {
                    TraverseAndUpdateResouceFileList(build,userSpecifiedDir, file, pathBase, listener, containerSASWithPlaceHolder);
                }
                else {
                    String filePath = file.getCanonicalPath();
                    String fileName = file.getName();
                    Utils.print(listener, file.getName());
                    String relativePath = new File(pathBase).toURI().relativize(new File(filePath).toURI()).getPath();
                    ResourceFile resourceFile = new ResourceFile();
                    String fileSAS = containerSASWithPlaceHolder.replace(placeHolder, userSpecifiedDir + "/" + relativePath);
                    resourceFile.setBlobSource(fileSAS);
                    resourceFile.setFilePath(fileName);
                    if(this.resourceFileList == null) {
                        Utils.print(listener, "Resouce File is still null");
                    }
                    Utils.print(listener,"Resource File Path: " + resourceFile.getFilePath());

                    resourceFileList.add(resourceFile);
                    Utils.print(listener, relativePath);
                    Utils.print(listener, fileSAS);
                }
            }
        } catch (IOException e) {
            Utils.print(listener, e.getStackTrace().toString());
        }
    }

    private CloudPool CreatePool(AbstractBuild<?, ?> build, BatchClient client, String poolId, BuildListener listener) throws BatchErrorException, IllegalArgumentException, IOException, InterruptedException {

        listener.getLogger().println("Creating Pool" + poolId);
        List<CloudPool> poolCollection = new ArrayList<CloudPool>();
        //List<CloudPool> poolCollection = client.getPoolOperations().listPools();

        listener.getLogger().println("Pool listing finished");
        try {
            poolCollection = client.getPoolOperations().listPools(new DetailLevel.Builder().filterClause(String.format("id eq '%s'", poolId)).build());
        } catch (Exception e) {
            TakeActionFromException(build, listener, e, Result.FAILURE);
        }
        if (poolCollection != null & poolCollection.isEmpty()) {
            listener.getLogger().println(String.format("There isn't any pool with %s, hence creating pool...",poolId));
            client.getPoolOperations().createPool(poolId, "4", "small", 4);
        }

        for (Iterator<CloudPool> p = poolCollection.iterator(); p.hasNext(); ) {
            listener.getLogger().println(p.next().getId());
            //listener.getLogger().println(p.next().getDisplayName());
        }

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        boolean steady = false;

        while (elapsedTime < 5 * 60 * 1000) {
            CloudPool pool = client.getPoolOperations().getPool(poolId);
            if (pool.getAllocationState() == AllocationState.STEADY) {
                steady = true;
                break;
            }
            listener.getLogger().println("Waiting for pool to get steady...");
            Thread.sleep(30 * 1000);
            elapsedTime = (new Date()).getTime() - startTime;
        }

        if (!steady) {
            listener.getLogger().println("Pool isn't steady yet");
            return null;
        }

        startTime = System.currentTimeMillis();
        elapsedTime = 0L;
        steady = false;

        while (elapsedTime < 10 * 60 * 1000) {

            boolean allIdle = true;
            List<ComputeNode> nodeCollection = client.getComputeNodeOperations().listComputeNodes(poolId);
            for (ComputeNode node : nodeCollection) {
                if (node.getState() != ComputeNodeState.IDLE) {
                    allIdle = false;
                    break;
                }
            }

            if (allIdle) {
                // BUG here: need loop ComputeNodes.ListNext
                steady = true;
                break;
            }

            listener.getLogger().println("Waiting for tvm to start...");
            Thread.sleep(30 * 1000);
            elapsedTime = (new Date()).getTime() - startTime;
        }

        if (!steady) {
            listener.getLogger().println("TVMs are not ready yet");
            return null;
        }

        listener.getLogger().println("Using Pool with Id:" + poolId);
        return client.getPoolOperations().getPool(poolId);
    }

    @Extension
    public static final class TestInParallelPostBuildDescriptor extends
            BuildStepDescriptor<Publisher> {

        private final CopyOnWriteList<BatchAccountInfo> batchAccounts = new CopyOnWriteList<BatchAccountInfo>();
        private final CopyOnWriteList<StorageAccountInfo> storageAccounts = new CopyOnWriteList<StorageAccountInfo>();

        public TestInParallelPostBuildDescriptor() {
            super();
            load();
        }
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException{
            batchAccounts.replaceBy(req.bindParametersToList(BatchAccountInfo.class, "azure_"));
            storageAccounts.replaceBy(req.bindParametersToList(StorageAccountInfo.class, "azure_"));
            save();
            return super.configure(req, formData);
        }

        public BatchAccountInfo[] getBatchAccounts() {
            return batchAccounts
                    .toArray(new BatchAccountInfo[batchAccounts.size()]);
        }

        public StorageAccountInfo[] getStorageAccounts() {
            return storageAccounts
                    .toArray(new StorageAccountInfo[storageAccounts.size()]);
        }

        public BatchAccountInfo getBatchAccount(String name) {

            if (name == null || (name.trim().length() == 0)) {
                return null;
            }

            BatchAccountInfo batchAccountInfo = null;
            BatchAccountInfo[] batchAccounts = getBatchAccounts();

            if (batchAccounts != null) {
                for (BatchAccountInfo ba : batchAccounts) {
                    if (ba.getBatchAccountName().equals(name)) {
                        batchAccountInfo = ba;

                        }
                        break;
                    }

                }
            return batchAccountInfo;
        }

        public StorageAccountInfo getStorageAccount(String name) {

            if (name == null || (name.trim().length() == 0)) {
                return null;
            }

            StorageAccountInfo storageAccountInfo = null;
            StorageAccountInfo[] storageAccounts = getStorageAccounts();

            if (storageAccounts != null) {
                for (StorageAccountInfo sa : storageAccounts) {
                    if (sa.getStorageAccName().equals(name)) {
                        storageAccountInfo = sa;
                    }
                    break;
                }
            }
            return storageAccountInfo;
        }

        public ListBoxModel doFillBatchAccountItems() {
            ListBoxModel m = new ListBoxModel();
            BatchAccountInfo[] BatchAccounts = getBatchAccounts();

            if (BatchAccounts != null) {
                for (BatchAccountInfo batchAccount : BatchAccounts) {
                    m.add(batchAccount.getBatchAccountName());
                }
            }
            return m;
        }

        public ListBoxModel doFillStorageAccountItems() {
            ListBoxModel m = new ListBoxModel();
            StorageAccountInfo[] StorageAccounts = getStorageAccounts();

            if (StorageAccounts != null) {
                for (StorageAccountInfo storageAccount : StorageAccounts) {
                    m.add(storageAccount.getStorageAccName());
                }
            }
            return m;
        }

        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return "Microsoft-AzureBatch - Execute tests in parallel";
        }
    }
}

