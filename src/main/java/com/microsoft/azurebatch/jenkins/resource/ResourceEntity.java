/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.resource;

/**
 * ResourceEntity interface
 */
public interface ResourceEntity {
    
    /**
     * Get the resource name.
     * @return resource name.
     */
    public String getResourceName();
    
    /**
     * Get source path of this resource. 
     * @return source path, null if resource is Azure Storage blob resource.
     */
    public String getSourcePath();
    
    /**
     * Check if require zip the resource on Jenkins server.
     * @return true if required.
     */
    public boolean requireZip();
    
    /**
     * Check if require unzip the resource on target VM.
     * @return true if required.
     */
    public boolean requireUnzip();
    
    /**
     * Get the blob path on Azure Storage.
     * @return blob path.
     */
    public String getBlobPath();
    
    /**
     * Set the uploaded blob path on Azure Storage.
     * @param blobPath blob path on Azure Storage.
     */
    public void setBlobPath(String blobPath);
    
    /**
     * Get the blob name on Azure Storage.
     * @return blob name.
     */
    public String getBlobName();
}
