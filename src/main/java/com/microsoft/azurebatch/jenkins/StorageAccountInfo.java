package com.microsoft.azurebatch.jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by nirajg on 3/30/2016.
 */
public class StorageAccountInfo {

    /** Windows Azure storage account name. */
    private String 			storageAccName;

    /** Windows azure storage account primary access key. */
    private String 			storageAccountKey;

    /** Windows Azure stoarge blob end point url. */
    private String 			blobEndPointURL;

    @DataBoundConstructor
    public StorageAccountInfo(final String storageAccName, final String storageAccountKey,
                              final String blobEndPointURL) {
        this.storageAccName 	= storageAccName;
        this.blobEndPointURL    = blobEndPointURL;
        this.storageAccountKey	= storageAccountKey;
    }

    public String getStorageAccName() {
        return storageAccName;
    }

    public void setStorageAccName(final String storageAccName) {
        this.storageAccName = storageAccName;
    }

    public String getStorageAccountKey() {
        return storageAccountKey;
    }

    public void setStorageAccountKey(final String storageAccountKey) {
        this.storageAccountKey = storageAccountKey;
    }

    public String getBlobEndPointURL() {
        return blobEndPointURL;
    }

    public void setBlobEndPointURL(final String blobEndPointURL) {
        this.blobEndPointURL = blobEndPointURL;
    }
}
