/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.jobsplitter;

import com.microsoft.azurebatch.jenkins.azurebatch.TaskDefinition;
import com.microsoft.azurebatch.jenkins.jobsplitter.autogen.JobSplitter;
import com.microsoft.azurebatch.jenkins.jobsplitter.autogen.Task;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JobSplitterHelper class
 */
public class JobSplitterHelper 
{
    private JobSplitter jobSplitter = null;
    
    /**
     * JobSplitterHelper constructor
     * @param listener BuildListener
     * @param jobSplitterFile jobSplitter file 
     * @throws IOException
     */
    public JobSplitterHelper(BuildListener listener, String jobSplitterFile) throws IOException 
    {
        jobSplitter = JobSplitterFactory.generateJobSplitter(listener, jobSplitterFile); 
    }
    
    /**
     * Create TaskDefinition list
     * @return TaskDefinition list
     */
    public List<TaskDefinition> createTaskDefinitionList()
    {
        List<TaskDefinition> taskList = new ArrayList<>();  
        
        for(Task task :this.jobSplitter.getJobConfigs().getTasks()) {
            List<String> commands = new ArrayList<>();
            
            for(String command : task.getCommands()) {
                for(com.microsoft.azurebatch.jenkins.jobsplitter.autogen.TaskCommandDictionary d 
                    : this.jobSplitter.getTaskCommandDictionary())
                {
                    command = command.replace(d.getKey(), d.getValue());
                }
                commands.add(command);
            }
            
            int taskTimeoutInMins = task.getTimeOutMinutes();
            if (taskTimeoutInMins <= 0)
                taskTimeoutInMins = this.jobSplitter.getJobConfigs().getDefaultTaskTimeoutInMinutes();
            
            taskList.add(new TaskDefinition(task.getName(), commands, taskTimeoutInMins));
        }

        return taskList;
    }

    /**
     * Get job timeout in minutes
     * @return job timeout in minutes
     */
    public int getJobTimeoutInMinutes()
    {
        return this.jobSplitter.getJobConfigs().getJobTimeoutInMinutes();
    }      
}
