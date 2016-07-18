/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.resource;

public abstract class BaseResourceEntity implements ResourceEntity {
    
    protected String resourceName;
    private String blobPath;
    private String blobName;

    /**
     * @return the resourceName
     */
    @Override
    public final String getResourceName() {
        return resourceName;
    }

    /**
     * @return the blobPath
     */
    @Override
    public final String getBlobPath() {
        return blobPath;
    }

    /**
     * @param blobPath the blobPath to set
     */
    @Override
    public final void setBlobPath(String blobPath) {
        this.blobPath = blobPath;
    }

    /**
     * @return the blobName
     */
    @Override
    public final String getBlobName() {
        return blobName;
    }
    
    protected final void setBlobName(String blobName) {
        this.blobName = blobName;
    }
    
    public static boolean containsSas(String blobPath) {
        return blobPath.contains("?");
    }
}
