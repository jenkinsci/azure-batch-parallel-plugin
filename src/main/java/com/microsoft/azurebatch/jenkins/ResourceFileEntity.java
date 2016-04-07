package com.microsoft.azurebatch.jenkins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

/**
 * Created by nirajg on 3/19/2016.
 */
// TODO: FIX NAME
class ResourceFileEntity {
    //TODO: Add get/set

    public String name;
    public String unZippedfilePath;
    public String zippedFilePath;
    public String blobPath;
    public String blobSAS;

    public ResourceFileEntity(String name, String unZippedfilePath, String zippedFilePath, String blobPath, String blobSAS) {
        this.name = name;
        this.unZippedfilePath = unZippedfilePath;
        this.zippedFilePath = zippedFilePath;
        this.blobPath = blobPath;
        this.blobSAS = blobSAS;
    }
}
        // TODO: FIX NAME
        //TODO: Add get/set
    class ResourceFileEntityNonZipped {
        public String name;
        public String filePath;
        public String blobPath;
        public String blobSAS;

        public ResourceFileEntityNonZipped(String name, String filePath, String blobPath, String blobSAS)
        {
            this.name = name;
            this.filePath = filePath;
            this.blobPath = blobPath;
            this.blobSAS = blobSAS;
        }
}
class TaskGroupDefinition {
    //TODO: Add get/set
    public Map<String,ResourceFileEntity> ResourceFileEntityMap;
    public Map<String, ResourceFileEntityNonZipped> ResourceFileEntityNonZippedMap;
    public List<TaskDefinition> TaskList;
    public boolean Skip;
    public List<String> SkipReasons;
    public String TaskGroupGuid;

    public TaskGroupDefinition() {
        this.ResourceFileEntityMap = new HashMap<String, ResourceFileEntity>();
        this.ResourceFileEntityNonZippedMap = new HashMap<String, ResourceFileEntityNonZipped>();
        this.SkipReasons = new ArrayList<String>();
        this.TaskGroupGuid = UUID.randomUUID().toString();
        this.TaskList = new ArrayList<TaskDefinition>();
    }

}

class TaskDefinition {
    public String TaskCommand;
    public String TaskId; //Guid

    public TaskDefinition(String TaskCommand, String TaskId)
    {
        this.TaskCommand = TaskCommand;
        this.TaskId = TaskId;
    }
}
