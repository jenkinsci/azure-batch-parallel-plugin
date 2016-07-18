/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.jobsplitter.autogen;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * TaskCommandDictionary schema.
 * <p>
 * TaskCommandDictionary to define the phrases to be replaced in jobConfigs-tasks-commands.
 * 
 */
@Generated("org.jsonschema2pojo")
public class TaskCommandDictionary {

    /**
     * Key schema.
     * <p>
     * The string to be replaced.
     * (Required)
     * 
     */
    @SerializedName("key")
    @Expose
    private String key;
    /**
     * Value schema.
     * <p>
     * The string to replace the key string.
     * (Required)
     * 
     */
    @SerializedName("value")
    @Expose
    private String value;

    /**
     * Key schema.
     * <p>
     * The string to be replaced.
     * (Required)
     * 
     * @return
     *     The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Key schema.
     * <p>
     * The string to be replaced.
     * (Required)
     * 
     * @param key
     *     The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Value schema.
     * <p>
     * The string to replace the key string.
     * (Required)
     * 
     * @return
     *     The value
     */
    public String getValue() {
        return value;
    }

    /**
     * Value schema.
     * <p>
     * The string to replace the key string.
     * (Required)
     * 
     * @param value
     *     The value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
