package com.microsoft.azurebatch.jenkins;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "TasksDescription")
public class TasksDescription {
    @JacksonXmlProperty(localName = "OwnerName")
    private String ownerName;
    public String getOwnerName() { return ownerName; }

    @JacksonXmlProperty(localName = "TaskGroup")
    private Tasks taskGroup;
    public Tasks getTaskGroup() { return taskGroup; }

    @Override
    public String toString() {
        return "Ownername=" + getOwnerName() + "; " + taskGroup.toString();
    }

    public static class Tasks {
        @JacksonXmlProperty(localName = "Task")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Task> tasks;
        public List<Task> getTasks() { return tasks; }

        @Override
        public String toString() {
            return "Tasks = " + tasks.toString();
        }

        public static class Task {
            @JacksonXmlProperty(localName = "Command")
            private String command;
            public String getCommand() { return command; }

            @Override
            public String toString() {
                return "Task: Command = " + command + "\n";
            }
        }
    }
}