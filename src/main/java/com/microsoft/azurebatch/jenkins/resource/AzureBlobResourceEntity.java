/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.resource;

import java.io.FileNotFoundException;

/**
 * AzureBlobResourceEntity class
 */
public class AzureBlobResourceEntity extends BaseResourceEntity {
        
    private final boolean requireUnzip;
    
    private boolean containsSasKey;
    
    private String storageAccount;
    
    private String containerName;
        
    /**
     * AzureBlobResourceEntity constuctor
     * @param blobPath blob path
     * @param sasKey SAS key
     * @param requireUnzip whether require unzip the resource
     * @throws FileNotFoundException
     */
    public AzureBlobResourceEntity(String blobPath, String sasKey, boolean requireUnzip) throws FileNotFoundException {
        setBlobPath(blobPath);
        if (sasKey != null) {
            setSasKey(sasKey);
        }
        this.requireUnzip = requireUnzip;
        
        parseBlobPath();
    }
    
    private void parseBlobPath() {
        String blobPath = getBlobPath();
        
        String error = String.format("blobPath %s is invalid.", blobPath);
        
        // Parse blobName, storageAccount, resourceName, container,
        // blobPath is in format like: http(s)://[storageAccount].blob.core.windows.net/[container]/.../[blobName]
        
        int idx = blobPath.indexOf("//");
        if (idx < 0) {
            throw new IllegalArgumentException(error);
        }
        idx += "//".length();
        
        int idxEnd = blobPath.indexOf(".", idx);
        if (idxEnd < 0) {
            throw new IllegalArgumentException(error);
        }
        storageAccount = blobPath.substring(idx, idxEnd);
        
        idx = blobPath.indexOf("/", idx);
        if (idx < 0) {
            throw new IllegalArgumentException(error);
        }
        idx += "/".length();
        
        idxEnd = blobPath.indexOf("/", idx);
        if (idxEnd < 0) {
            throw new IllegalArgumentException(error);
        }
        containerName = blobPath.substring(idx, idxEnd);
        
        idx = blobPath.lastIndexOf('/') + 1;
        setBlobName(blobPath.substring(idx));
        
        resourceName = getBlobName();
    }

    /**
     * @return the sourcePath
     */
    @Override
    public String getSourcePath() {
        throw new UnsupportedOperationException("getSourcePath() is not available.");
    }

    @Override
    public boolean requireZip() {
        return false;
    }

    @Override
    public boolean requireUnzip() {
        return requireUnzip;
    }

    /**
     * @return the storageAccount
     */
    public String getStorageAccount() {
        return storageAccount;
    }

    /**
     * @return the containerName
     */
    public String getContainerName() {
        return containerName;
    }
    
    /**
     * Whether contains SAS key
     * @return whether contains SAS key
     */
    public boolean containsSasKey() {
        return containsSasKey;
    }
    
    /**
     * Set SAS key
     * @param sasKey SAS key
     */
    public final void setSasKey(String sasKey) {
        if (containsSasKey()) {
            throw new IllegalArgumentException("SAS key is already set for this Resourece Entity.");
        }
        
        this.containsSasKey = true;
        if (!sasKey.isEmpty()) {
            setBlobPath(getBlobPath() + "?" + sasKey);
        }        
    }
}
