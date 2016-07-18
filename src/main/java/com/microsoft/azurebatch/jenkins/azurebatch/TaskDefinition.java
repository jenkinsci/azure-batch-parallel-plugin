/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.azurebatch;

import java.util.List;
import java.util.UUID;

/**
 * Task definition class
 */
public class TaskDefinition {
    private final String name;
    private final String taskId;
    private final int timeoutInMins;
    private final List<String> commands;

    /**
     * TaskDefinition constructor
     * @param name task name
     * @param commands task commands
     * @param timeoutInMins task timeout in minutes
     */
    public TaskDefinition(String name, List<String> commands, int timeoutInMins)
    {
        this.name = name;
        this.taskId = UUID.randomUUID().toString();
        this.commands = commands;
        this.timeoutInMins = timeoutInMins;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the timeoutInMins
     */
    public int getTimeoutInMins() {
        return timeoutInMins;
    }

    /**
     * @return the commands
     */
    public List<String> getCommands() {
        return commands;
    }
}
