/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.jobsplitter.autogen;

import com.microsoft.azurebatch.jenkins.jobsplitter.autogen.Task;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * JobConfigs schema.
 * <p>
 * Test job configs.
 * 
 */
@Generated("org.jsonschema2pojo")
public class JobConfigs {

    /**
     * JobTimeoutInMinutes schema.
     * <p>
     * The whole job time out in minutes.
     * (Required)
     * 
     */
    @SerializedName("jobTimeoutInMinutes")
    @Expose
    private int jobTimeoutInMinutes;
    /**
     * DefaultTaskTimeoutInMinutes schema.
     * <p>
     * Default timeout in minutes for task.
     * (Required)
     * 
     */
    @SerializedName("defaultTaskTimeoutInMinutes")
    @Expose
    private int defaultTaskTimeoutInMinutes;
    /**
     * Tasks schema.
     * <p>
     * Tasks to run on VM.
     * (Required)
     * 
     */
    @SerializedName("tasks")
    @Expose
    private List<Task> tasks = new ArrayList<Task>();

    /**
     * JobTimeoutInMinutes schema.
     * <p>
     * The whole job time out in minutes.
     * (Required)
     * 
     * @return
     *     The jobTimeoutInMinutes
     */
    public int getJobTimeoutInMinutes() {
        return jobTimeoutInMinutes;
    }

    /**
     * JobTimeoutInMinutes schema.
     * <p>
     * The whole job time out in minutes.
     * (Required)
     * 
     * @param jobTimeoutInMinutes
     *     The jobTimeoutInMinutes
     */
    public void setJobTimeoutInMinutes(int jobTimeoutInMinutes) {
        this.jobTimeoutInMinutes = jobTimeoutInMinutes;
    }

    /**
     * DefaultTaskTimeoutInMinutes schema.
     * <p>
     * Default timeout in minutes for task.
     * (Required)
     * 
     * @return
     *     The defaultTaskTimeoutInMinutes
     */
    public int getDefaultTaskTimeoutInMinutes() {
        return defaultTaskTimeoutInMinutes;
    }

    /**
     * DefaultTaskTimeoutInMinutes schema.
     * <p>
     * Default timeout in minutes for task.
     * (Required)
     * 
     * @param defaultTaskTimeoutInMinutes
     *     The defaultTaskTimeoutInMinutes
     */
    public void setDefaultTaskTimeoutInMinutes(int defaultTaskTimeoutInMinutes) {
        this.defaultTaskTimeoutInMinutes = defaultTaskTimeoutInMinutes;
    }

    /**
     * Tasks schema.
     * <p>
     * Tasks to run on VM.
     * (Required)
     * 
     * @return
     *     The tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Tasks schema.
     * <p>
     * Tasks to run on VM.
     * (Required)
     * 
     * @param tasks
     *     The tasks
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

}
