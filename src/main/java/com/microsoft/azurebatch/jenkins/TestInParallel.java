package com.microsoft.azurebatch.jenkins;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import org.apache.commons.io.IOUtils;

import com.microsoft.azure.batch.*;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.protocol.models.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Throwable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link TestInParallel} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #batchAccount})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Niraj Gandhi
 */
public class TestInParallel extends Builder implements SimpleBuildStep {

    private final String batchAccount;
    private final String batchAccountKey;
    private final String batchServiceUrl;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public TestInParallel(String batchAccount, String batchAccountKey, String batchServiceUrl) {
        this.batchAccount = batchAccount;
        this.batchAccountKey = batchAccountKey;
        this.batchServiceUrl = batchServiceUrl;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return batchAccount;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        // This also shows how you can consult the global configuration of the builder

        // TODO: Convert Enum to String, instead of this bad code
        /*
        if (getDescriptor().getTestHarness() == TestHarness.xUnit) {

                //Add PoolStartup task to download xUnit Nuget package
            }
            else
            {
                listener.getLogger().println("Test Harness:" + "NotSupported");
            }
        */

        String batchAccount = "nirajgauseast";
        listener.getLogger().println("Hello, "+ batchAccount +"!");
        listener.getLogger().println("Initiating Test in Parallel with Azure Batch");


        //BatchSharedKeyCredentials cred = new BatchSharedKeyCredentials(this.batchServiceUrl, this.batchAccount, this.batchAccountKey);
        BatchSharedKeyCredentials cred = new BatchSharedKeyCredentials("https://nirajgauseast.australiaeast.batch.azure.com", batchAccount , "2SvLk15driLhqdfCnf5tGFJQS2+Re6x7U6c9rqI5O5zpf23VeArvlX0AvRnYkvU+1+1m0gHFLVZCpHSPSFR/BQ==");

        BatchClient client = BatchClient.Open(cred);
        String userName = System.getProperty("user.name");
        String poolId = batchAccount + "-jenkinspool";
        listener.getLogger().println("PoolName: " + poolId);
        String jobId = batchAccount + UUID.randomUUID().toString();

        try {
            listener.getLogger().println("Creating Azure Batch Pool...");
                CloudPool sharedPool = CreatePool(client, poolId, listener);

            PoolInformation poolInfo = new PoolInformation();
            poolInfo.setPoolId(sharedPool.getId());
            listener.getLogger().println(String.format("Pool with id: %s got created", sharedPool.getId()));
            listener.getLogger().println("Creating Azure Batch Job with JobId:" + jobId);

            client.getJobOperations().createJob(jobId, poolInfo);
            listener.getLogger().println(String.format("Job with Id: %s is created", jobId));

            CloudJob boundJob = client.getJobOperations().getJob(jobId);

            // get an empty unbound Task
            String taskId = "waitforsometime";

            String linuxIaasHWTaskCmdLine = "PING 1.1.1.1 -n 1 -w 3000";
            String winnerTaskCmdLine = linuxIaasHWTaskCmdLine;;


            TaskAddParameter hwTask = new TaskAddParameter();
            hwTask.setId(taskId);
            hwTask.setCommandLine(winnerTaskCmdLine);


            ResourceFile file = new ResourceFile();
            file.setBlobSource("http://nirajgdemo.blob.core.windows.net/nirajgdemo/NuGet.exe?sv=2014-02-14&sr=c&sig=4xvjP5G0kGjj7f3uStppTcRnHhdP5ZmgPu3%2ByaglbS8%3D&st=2016-03-14T07%3A00%3A00Z&se=2016-09-02T07%3A00%3A00Z&sp=r");
            file.setFilePath("NuGet.exe");

            List<ResourceFile> fileList = new ArrayList<ResourceFile>();
            fileList.add(file);

            hwTask.setResourceFiles(fileList);

            
            // add Task to Job
            //TaskAddOptions addTaskOptions = new TaskAddOptions();
            //addTaskOptions.setClientRequestId(UUID.randomUUID().toString());
            client.getTaskOperations().createTask(boundJob.getId(), hwTask);

            // wait for the task to complete
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            boolean steady = false;

            while (elapsedTime < 10*60*1000) {
                List<CloudTask> taskCollection = client.getTaskOperations().listTasks(boundJob.getId());

                boolean allComplete = true;
                for (CloudTask task : taskCollection) {
                    listener.getLogger().println("Task Detail:" + task.getId());
                    if (task.getState() != TaskState.COMPLETED) {
                        allComplete = false;
                        break;
                    }
                }
                if (allComplete) {
                    steady = true;
                    break;
                }

               listener.getLogger().println("wait for tasks complete");
                Thread.sleep(30 * 1000);
                elapsedTime = (new Date()).getTime() - startTime;
            }

            if (!steady) {
               listener.getLogger().println("TSM timed out in Helloworld.");
                return;
            }

            CloudTask boundTask = client.getTaskOperations().getTask(boundJob.getId(), hwTask.getId());

            String StandardOutFileName = "stdout.txt";
            InputStream stream = client.getFileOperations().getFileFromTask(boundJob.getId(), boundTask.getId(), StandardOutFileName);
            Charset encoding = Charset.defaultCharset();
            String theString = IOUtils.toString(stream, encoding);
           listener.getLogger().println(theString);

            FileProperties property = client.getFileOperations().getFilePropertiesFromTask(boundJob.getId(), boundTask.getId(), StandardOutFileName);
           listener.getLogger().println(property.getContentLength());

            JobPatchParameter jobPatchParameter = new JobPatchParameter();
            List<MetadataItem> metadata = new ArrayList<MetadataItem>();
            MetadataItem md = new MetadataItem();
            md.setName("abc");
            md.setValue("def");
            metadata.add(md);
            jobPatchParameter.setMetadata(metadata);

            client.getJobOperations().patchJob(jobId, jobPatchParameter);

            CloudJob patchedJob = client.getJobOperations().getJob(jobId);
           listener.getLogger().println(patchedJob.getMetadata().size());

        }

        catch (BatchErrorException e) {
            listener.getLogger().println("Found exception");
            e.printStackTrace(listener.getLogger());
            listener.getLogger().println(String.format("BatchError %s", e.toString()));
            listener.getLogger().println(String.format("BatchError code = %s, message = %s", e.getBody().getCode(), e.getBody().getMessage()));
            for (BatchErrorDetail detail : e.getBody().getValues()) {
                listener.getLogger().println(String.format("Detail %s=%s", detail.getKey(), detail.getValue()));
            }
            
            // listener.getLogger().println(e.getBody().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            try {
                listener.getLogger().println("Deleting job with id : " + jobId);
                client.getJobOperations().deleteJob(jobId);
                listener.getLogger().println("Deleted Job with id : " + jobId);
            }
            catch (BatchErrorException err) {
               listener.getLogger().println(String.format("BatchError %s", err.getMessage()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        listener.getLogger().println("Created client");
    }

    private static CloudPool CreatePool(BatchClient client, String poolId, TaskListener listener) throws BatchErrorException, IllegalArgumentException, IOException, InterruptedException {

        listener.getLogger().println("Creating Pool" + poolId);
        List<CloudPool> poolCollection = client.getPoolOperations().listPools();
        try {
            poolCollection   = client.getPoolOperations().listPools(new DetailLevel.Builder().filterClause(String.format("id eq '%s'", poolId)).build());
        }
        catch(Exception e)
        {
            listener.getLogger().println(e.getStackTrace());
            listener.getLogger().println(e.getMessage());
        }
         if (poolCollection.isEmpty()) {
            listener.getLogger().println("no pool with this name");
            client.getPoolOperations().createPool(poolId, "4", "small", 1);
        }

        for(Iterator<CloudPool> p = poolCollection.iterator();p.hasNext();)
        {
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
           listener.getLogger().println("wait for pool steady");
            Thread.sleep(30 * 1000);
            elapsedTime = (new Date()).getTime() - startTime;
        }

        if (!steady) {
           listener.getLogger().println("Pool wasn't steady at time");
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

           listener.getLogger().println("wait for tvm start");
            Thread.sleep(30 * 1000);
            elapsedTime = (new Date()).getTime() - startTime;
        }

        if (!steady) {
           listener.getLogger().println("TVM wasn't ready at time");
            return null;
        }

        listener.getLogger().println("PoolId:" + poolId);
        return client.getPoolOperations().getPool(poolId);
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link TestInParallel}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useXUnit;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
        //TODO: this is broken from HelloWorld - need to be fixed
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Test in Parallel with Azure Batch";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useXUnit = formData.getBoolean("useXUnit");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public TestHarness getTestHarness() {
            if(useXUnit)
            {
                return TestHarness.xUnit;
            }
            return TestHarness.NotSupported;

        }
    }
}

