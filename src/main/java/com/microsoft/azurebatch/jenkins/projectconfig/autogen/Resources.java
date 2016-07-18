/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig.autogen;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Resources schema.
 * <p>
 * Resources folders and files will be copied to task VMs under folder %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID% before any test running.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Resources {

    /**
     * LocalResources schema.
     * <p>
     * Files and Folders on local disk or remote share.
     * 
     */
    @SerializedName("localResources")
    @Expose
    private List<LocalResource> localResources = new ArrayList<LocalResource>();
    /**
     * AzureStorageBlobs schema.
     * <p>
     * Azure storage blob resources.
     * 
     */
    @SerializedName("azureStorageBlobs")
    @Expose
    private List<AzureStorageBlob> azureStorageBlobs = new ArrayList<AzureStorageBlob>();

    /**
     * LocalResources schema.
     * <p>
     * Files and Folders on local disk or remote share.
     * 
     * @return
     *     The localResources
     */
    public List<LocalResource> getLocalResources() {
        return localResources;
    }

    /**
     * LocalResources schema.
     * <p>
     * Files and Folders on local disk or remote share.
     * 
     * @param localResources
     *     The localResources
     */
    public void setLocalResources(List<LocalResource> localResources) {
        this.localResources = localResources;
    }

    /**
     * AzureStorageBlobs schema.
     * <p>
     * Azure storage blob resources.
     * 
     * @return
     *     The azureStorageBlobs
     */
    public List<AzureStorageBlob> getAzureStorageBlobs() {
        return azureStorageBlobs;
    }

    /**
     * AzureStorageBlobs schema.
     * <p>
     * Azure storage blob resources.
     * 
     * @param azureStorageBlobs
     *     The azureStorageBlobs
     */
    public void setAzureStorageBlobs(List<AzureStorageBlob> azureStorageBlobs) {
        this.azureStorageBlobs = azureStorageBlobs;
    }

}
