package com.microsoft.azurebatch.jenkins;

import java.net.URISyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.microsoft.windowsazure.storage.CloudStorageAccount;
import com.microsoft.windowsazure.storage.RetryNoRetry;
import com.microsoft.windowsazure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.windowsazure.storage.StorageException;
import com.microsoft.windowsazure.storage.blob.BlobContainerPermissions;
import com.microsoft.windowsazure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.windowsazure.storage.blob.BlobRequestOptions;
import com.microsoft.windowsazure.storage.blob.CloudBlob;
import com.microsoft.windowsazure.storage.blob.CloudBlobClient;
import com.microsoft.windowsazure.storage.blob.CloudBlobContainer;
import com.microsoft.windowsazure.storage.blob.CloudBlobDirectory;
import com.microsoft.windowsazure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.storage.blob.ListBlobItem;
import com.microsoft.windowsazure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.windowsazure.storage.blob.SharedAccessBlobPolicy;
import hudson.FilePath;
import hudson.model.BuildListener;
//import com.microsoftopentechnologies.windowsazurestorage.WAStoragePublisher.UploadType;
//import com.microsoftopentechnologies.windowsazurestorage.beans.StorageAccountInfo;
//import com.microsoftopentechnologies.windowsazurestorage.exceptions.WAStorageException;
//import com.microsoftopentechnologies.windowsazurestorage.helper.Utils;

/**
 * Created by nirajg on 3/17/2016.
 */
public class AzureStorageHelper {
    public static final String DEF_BLOB_URL = "http://blob.core.windows.net/";
    private static final String QUEUE = "queue";
    private static final String TABLE = "table";
    private static final String BLOB = "blob";

    public static CloudBlobContainer  getBlobContainerReference(String accName,
                                                                String key, String blobURL, String containerName,
                                                                boolean createCnt, boolean allowRetry, Boolean cntPubAccess, BuildListener listener)
            throws URISyntaxException, StorageException {

        CloudStorageAccount cloudStorageAccount;
        CloudBlobClient serviceClient;
        CloudBlobContainer container;
        StorageCredentialsAccountAndKey credentials;

        credentials = new StorageCredentialsAccountAndKey(accName, key);

        if (isNullOrEmpty(blobURL) || blobURL.equals(DEF_BLOB_URL)) {
            cloudStorageAccount = new CloudStorageAccount(credentials);
        } else {
            cloudStorageAccount = new CloudStorageAccount(credentials, new URI(
                    blobURL), new URI(getCustomURI(accName, QUEUE, blobURL)),
                    new URI(getCustomURI(accName, TABLE, blobURL)));
        }

        serviceClient = cloudStorageAccount.createCloudBlobClient();
        if (!allowRetry) {
            // Setting no retry policy
            RetryNoRetry rnr = new RetryNoRetry();
            serviceClient.setRetryPolicyFactory(rnr);
        }

        container = serviceClient.getContainerReference(containerName);

        boolean cntExists = container.exists();

        if (createCnt && !cntExists) {
            container.createIfNotExists();
        }

        // Apply permissions only if container is created newly
        if (!cntExists && cntPubAccess != null) {
            // Set access permissions on container.
            BlobContainerPermissions cntPerm;
            cntPerm = new BlobContainerPermissions();
            if (cntPubAccess) {
                cntPerm.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
            } else {
                cntPerm.setPublicAccess(BlobContainerPublicAccessType.OFF);
            }
            container.uploadPermissions(cntPerm);
        }
        return container;
    }


    public static URI upload(BuildListener listener, CloudBlockBlob blob, FilePath src)
            throws StorageException, IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = src.read();
        try {
            blob.upload(inputStream, src.length(), null,
                    getBlobRequestOptions(), null);
            long endTime = System.currentTimeMillis();
            Utils.print(listener, String.format("Uploaded blob with uri %s in %s", blob.getUri(), getTime(endTime - startTime)));
            return blob.getUri();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Utils.print(listener, e.getMessage());
            }
        }
    }

    public static String generateSASURL(BuildListener listener, String storageAccountName, String storageAccountKey, String containerName, String saBlobEndPoint) throws Exception {

        Utils.print(listener, "Creating SAS for container: "+ containerName);
        StorageCredentialsAccountAndKey credentials = new StorageCredentialsAccountAndKey(storageAccountName, storageAccountKey);
        URL blobURL = new  URL(saBlobEndPoint);
        String saBlobURI = 	new StringBuilder().append(blobURL.getProtocol()).append("://").append(storageAccountName).append(".")
                .append(blobURL.getHost()).append("/").toString();
        CloudStorageAccount cloudStorageAccount = new CloudStorageAccount(credentials, new URI(saBlobURI),
                new URI(getCustomURI(storageAccountName, QUEUE, saBlobURI)),
                new URI(getCustomURI(storageAccountName, TABLE, saBlobURI)));
        // Create the blob client.
        CloudBlobClient blobClient = cloudStorageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(containerName);

        // At this point need to throw an error back since container itself did not exist.
        if (!container.exists()) {
            throw new Exception("WAStorageClient: generateSASURL: Container " + containerName
                    + " does not exist in storage account " + storageAccountName);
        }

        SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());

        //policy.setSharedAccessStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR, 24);
        policy.setSharedAccessExpiryTime(calendar.getTime());
        policy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));

        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
        containerPermissions.getSharedAccessPolicies().put("jenkins"+System.currentTimeMillis(), policy);
        container.uploadPermissions(containerPermissions);

        // Create a shared access signature for the container.
        String sas = container.generateSharedAccessSignature(policy, null);
        Utils.print(listener, "Done: Creating SAS");
        return sas;
    }

    private static BlobRequestOptions getBlobRequestOptions() {
        BlobRequestOptions options = new BlobRequestOptions();

        int concurrentRequestCount = 1;

        try {
            concurrentRequestCount = Runtime.getRuntime().availableProcessors();
        } catch (Exception e) {
            e.printStackTrace();
        }

        options.setConcurrentRequestCount(concurrentRequestCount);

        return options;
    }

    public static String getTime(long timeInMills) {
        return DurationFormatUtils.formatDuration(timeInMills, "HH:mm:ss.S")
                + " (HH:mm:ss.S)";
    }

    private static String getCustomURI(String storageAccountName, String type,
                                       String blobURL) {

        if (QUEUE.equalsIgnoreCase(type)) {
            return blobURL.replace(storageAccountName + "." + BLOB,
                    storageAccountName + "." + type);
        } else if (TABLE.equalsIgnoreCase(type)) {
            return blobURL.replace(storageAccountName + "." + BLOB,
                    storageAccountName + "." + type);
        } else {
            return null;
        }
    }

    private static boolean isNullOrEmpty(final String name) {
        boolean isValid = false;
        if (name == null || name.matches("\\s*")) {
            isValid = true;
        }
        return isValid;
    }

    private static void print( BuildListener listener,String message) {
        listener.getLogger().println(message);
    }
}
