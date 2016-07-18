/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.resource;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * LocalResourceEntity class
 */
public class LocalResourceEntity extends BaseResourceEntity {
    // @sourcePath is absolute/relative file/folder path on Jenkins server.
    // The file/folder will be copied to %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID% folder on VMs.
    private String sourcePath;
    
    private boolean isSourceDirectory;
        
    /**
     * LocalResourceEntity constructor
     * @param sourcePath source path of the local resource
     * @throws FileNotFoundException
     */
    public LocalResourceEntity(String sourcePath) throws FileNotFoundException {
        File file = new File(sourcePath);
        
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("Resource %s not found.", sourcePath));
        }
        
        this.sourcePath = sourcePath;
        this.resourceName = file.getName();
        this.isSourceDirectory = file.isDirectory();
        
        if (isSourceDirectory) {
            setBlobName(getResourceName() + ".zip");
        } else {
            setBlobName(getResourceName());
        }
    }

    /**
     * @return the sourcePath
     */
    @Override
    public String getSourcePath() {
        return sourcePath;
    }

    @Override
    public boolean requireZip() {
        // Will zip resource if it's directory.
        return isSourceDirectory;
    }

    @Override
    public boolean requireUnzip() {
        // Will zip resource if it's directory.
        return isSourceDirectory;
    }
}
