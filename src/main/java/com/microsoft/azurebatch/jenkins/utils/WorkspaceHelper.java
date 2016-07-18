/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.utils;

import java.io.File;

/**
 * WorkspaceHelper class
 */
public final class WorkspaceHelper {    

    private final String workspacePath;
    private final String azureBatchTempDirName = "azurebatchtemp";
    private final String azureBatchTempDirPath;
    
    /**
     * WorkspaceHelper constructor
     * @param workspacePath workspace path
     */
    public WorkspaceHelper(String workspacePath) {
        this.workspacePath = workspacePath;
        azureBatchTempDirPath = getPathRelativeToWorkspace(azureBatchTempDirName);
    }
    
    /**
     * Get WorkspacePath
     * @return workspacePath
     */
    public String getWorkspacePath() {
        return workspacePath;
    }
    
    /**
     * Get temp folder path
     * @return temp folder path
     */
    public String getTempFolderPath() {
        return azureBatchTempDirPath;
    }
    
    /**
     * Get path relative to workspace folder
     * @param relativeToWorkspace the path relative to workspace folder
     * @return
     */
    public String getPathRelativeToWorkspace(String relativeToWorkspace) {
        return workspacePath + File.separator + relativeToWorkspace;
    }
    
    /**
     * Get path relative to temp folder
     * @param relativeToTempFolder path relative to temp folder
     * @return
     */
    public String getPathRelativeToTempFolder(String relativeToTempFolder) {
        return azureBatchTempDirPath + File.separator + relativeToTempFolder;
    }
}
