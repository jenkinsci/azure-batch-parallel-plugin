package com.microsoft.azurebatch.jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
/**
 * Created by nirajg on 3/29/2016.
 */
public class BatchAccountInfo {

    /** Azure Batch account name. */
    private String 			batchAccountName;

    /** Azure Batch account primary access key. */
    private String 			batchAccountKey;

    /** Azure Batch Service url. */
    private String 			batchServiceURL;

    @DataBoundConstructor
    public BatchAccountInfo(final String batchAccountName, final String batchAccountKey,
                              final String batchServiceURL) {
        this.batchAccountName 	= batchAccountName;
        this.batchAccountKey	= batchAccountKey;
        this.batchServiceURL    = batchServiceURL;
    }

    public String getBatchAccountName() {
        return batchAccountName;
    }

    public void setBatchAccountName(final String batchAccountName) {
        this.batchAccountName = batchAccountName;
    }

    public String getBatchAccountKey() {
        return batchAccountKey;
    }

    public void setBatchAccountKey(final String batchAccountKey) {
        this.batchAccountKey = batchAccountKey;
    }

    public String getBatchServiceURL() {
        return batchServiceURL;
    }

    public void setBatchServiceURL(final String batchServiceURL) {
        this.batchServiceURL = batchServiceURL;
    }
}

