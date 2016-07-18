/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig.autogen;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * AzureStorageBlob schema.
 * <p>
 * Azure storage blob resource.
 * 
 */
@Generated("org.jsonschema2pojo")
public class AzureStorageBlob {

    /**
     * Blob schema.
     * <p>
     * The Azure storage blob path, which may not contain SAS.
     * (Required)
     * 
     */
    @SerializedName("blob")
    @Expose
    private String blob;
    /**
     * SAS schema.
     * <p>
     * The Azure storage container/blob SAS for this blob. If not specified, plugin will try to generate container SAS for this blob with configured storage accouts. If it's public resourece, you may specify empty string as SAS.
     * 
     */
    @SerializedName("sas")
    @Expose
    private String sas;
    /**
     * Unzip schema.
     * <p>
     * Set to true if you want to unzip this blob on task VM. Default is false.
     * 
     */
    @SerializedName("unzip")
    @Expose
    private boolean unzip = false;

    /**
     * Blob schema.
     * <p>
     * The Azure storage blob path, which may not contain SAS.
     * (Required)
     * 
     * @return
     *     The blob
     */
    public String getBlob() {
        return blob;
    }

    /**
     * Blob schema.
     * <p>
     * The Azure storage blob path, which may not contain SAS.
     * (Required)
     * 
     * @param blob
     *     The blob
     */
    public void setBlob(String blob) {
        this.blob = blob;
    }

    /**
     * SAS schema.
     * <p>
     * The Azure storage container/blob SAS for this blob. If not specified, plugin will try to generate container SAS for this blob with configured storage accouts. If it's public resourece, you may specify empty string as SAS.
     * 
     * @return
     *     The sas
     */
    public String getSas() {
        return sas;
    }

    /**
     * SAS schema.
     * <p>
     * The Azure storage container/blob SAS for this blob. If not specified, plugin will try to generate container SAS for this blob with configured storage accouts. If it's public resourece, you may specify empty string as SAS.
     * 
     * @param sas
     *     The sas
     */
    public void setSas(String sas) {
        this.sas = sas;
    }

    /**
     * Unzip schema.
     * <p>
     * Set to true if you want to unzip this blob on task VM. Default is false.
     * 
     * @return
     *     The unzip
     */
    public boolean isUnzip() {
        return unzip;
    }

    /**
     * Unzip schema.
     * <p>
     * Set to true if you want to unzip this blob on task VM. Default is false.
     * 
     * @param unzip
     *     The unzip
     */
    public void setUnzip(boolean unzip) {
        this.unzip = unzip;
    }

}
