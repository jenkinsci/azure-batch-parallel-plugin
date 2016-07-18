/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.jobsplitter.autogen;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Task schema.
 * <p>
 * Task to run on VM.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Task {

    /**
     * Name schema.
     * <p>
     * Name of the task. If not specified, will use Task #index, for example, Task #1.
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Commands schema.
     * <p>
     * The commandlines to run on VM.
     * (Required)
     * 
     */
    @SerializedName("commands")
    @Expose
    private List<String> commands = new ArrayList<String>();
    /**
     * TimeOutMinutes schema.
     * <p>
     * Timeout in minutes of task. If not specified, will use defaultTaskTimeoutInMinutes.
     * 
     */
    @SerializedName("timeOutMinutes")
    @Expose
    private int timeOutMinutes;

    /**
     * Name schema.
     * <p>
     * Name of the task. If not specified, will use Task #index, for example, Task #1.
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * Name schema.
     * <p>
     * Name of the task. If not specified, will use Task #index, for example, Task #1.
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Commands schema.
     * <p>
     * The commandlines to run on VM.
     * (Required)
     * 
     * @return
     *     The commands
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * Commands schema.
     * <p>
     * The commandlines to run on VM.
     * (Required)
     * 
     * @param commands
     *     The commands
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * TimeOutMinutes schema.
     * <p>
     * Timeout in minutes of task. If not specified, will use defaultTaskTimeoutInMinutes.
     * 
     * @return
     *     The timeOutMinutes
     */
    public int getTimeOutMinutes() {
        return timeOutMinutes;
    }

    /**
     * TimeOutMinutes schema.
     * <p>
     * Timeout in minutes of task. If not specified, will use defaultTaskTimeoutInMinutes.
     * 
     * @param timeOutMinutes
     *     The timeOutMinutes
     */
    public void setTimeOutMinutes(int timeOutMinutes) {
        this.timeOutMinutes = timeOutMinutes;
    }

}
