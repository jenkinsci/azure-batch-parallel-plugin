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
 * LocalResource schema.
 * <p>
 * File or Folder on local disk or remote share.
 * 
 */
@Generated("org.jsonschema2pojo")
public class LocalResource {

    /**
     * Source schema.
     * <p>
     * File or folder's absolute path or relative path to Jenkins WORKSPACE folder.
     * (Required)
     * 
     */
    @SerializedName("source")
    @Expose
    private String source;

    /**
     * Source schema.
     * <p>
     * File or folder's absolute path or relative path to Jenkins WORKSPACE folder.
     * (Required)
     * 
     * @return
     *     The source
     */
    public String getSource() {
        return source;
    }

    /**
     * Source schema.
     * <p>
     * File or folder's absolute path or relative path to Jenkins WORKSPACE folder.
     * (Required)
     * 
     * @param source
     *     The source
     */
    public void setSource(String source) {
        this.source = source;
    }

}
