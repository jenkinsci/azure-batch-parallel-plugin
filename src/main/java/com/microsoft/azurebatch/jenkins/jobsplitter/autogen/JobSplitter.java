/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.jobsplitter.autogen;

import com.microsoft.azurebatch.jenkins.jobsplitter.autogen.TaskCommandDictionary;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Parallel testing plugin test split config schema.
 * <p>
 * Parallel testing Jenkins plugin with Microsoft Azure Batch service. This schema defines the test split configs for this plugin.
 * 
 */
@Generated("org.jsonschema2pojo")
public class JobSplitter {

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     */
    @SerializedName("version")
    @Expose
    private String version;
    /**
     * TaskCommandDictionary schema.
     * <p>
     * TaskCommandDictionary to define the phrases to be replaced in jobConfigs-tasks-commands.
     * 
     */
    @SerializedName("taskCommandDictionary")
    @Expose
    private List<TaskCommandDictionary> taskCommandDictionary = new ArrayList<TaskCommandDictionary>();
    /**
     * JobConfigs schema.
     * <p>
     * Test job configs.
     * (Required)
     * 
     */
    @SerializedName("jobConfigs")
    @Expose
    private JobConfigs jobConfigs;

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     * @return
     *     The version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     * @param version
     *     The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * TaskCommandDictionary schema.
     * <p>
     * TaskCommandDictionary to define the phrases to be replaced in jobConfigs-tasks-commands.
     * 
     * @return
     *     The taskCommandDictionary
     */
    public List<TaskCommandDictionary> getTaskCommandDictionary() {
        return taskCommandDictionary;
    }

    /**
     * TaskCommandDictionary schema.
     * <p>
     * TaskCommandDictionary to define the phrases to be replaced in jobConfigs-tasks-commands.
     * 
     * @param taskCommandDictionary
     *     The taskCommandDictionary
     */
    public void setDictionary(List<TaskCommandDictionary> taskCommandDictionary) {
        this.taskCommandDictionary = taskCommandDictionary;
    }

    /**
     * JobConfigs schema.
     * <p>
     * Test job configs.
     * (Required)
     * 
     * @return
     *     The jobConfigs
     */
    public JobConfigs getJobConfigs() {
        return jobConfigs;
    }

    /**
     * JobConfigs schema.
     * <p>
     * Test job configs.
     * (Required)
     * 
     * @param jobConfigs
     *     The jobConfigs
     */
    public void setJobConfigs(JobConfigs jobConfigs) {
        this.jobConfigs = jobConfigs;
    }

}
