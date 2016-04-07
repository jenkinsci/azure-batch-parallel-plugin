package com.microsoft.azurebatch.jenkins;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;

import hudson.model.TaskListener;

import com.microsoft.azure.batch.protocol.models.TaskAddParameter;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

/**
 * Created by krkotipa on 3/9/2016.
 */
public class TaskHelper {
    public static List<TaskAddParameter> GetTasksToRun(String taskDefinitionFile, TaskListener listener) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        String x = "";
        TasksDescription t = null;
        try {
            x = readFile(taskDefinitionFile, StandardCharsets.UTF_8);
            t = xmlMapper.readValue(x, TasksDescription.class);
        } catch(Exception e) {
            Logger.Log(listener, e);
        }

        List<TaskAddParameter> tasks = new ArrayList<TaskAddParameter>();
        for(TasksDescription.Tasks.Task task : t.getTaskGroup().getTasks()) {
            Logger.Log(listener, "Adding task: " + task.getCommand());
            TaskAddParameter taskAddParameter = new TaskAddParameter();
            taskAddParameter.setId(UUID.randomUUID().toString());
            taskAddParameter.setCommandLine(task.getCommand());
            tasks.add(taskAddParameter);
        }

        return tasks;
    }

    private static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}