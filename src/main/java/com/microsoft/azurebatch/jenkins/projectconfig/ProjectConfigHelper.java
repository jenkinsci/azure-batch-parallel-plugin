/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig;

import com.microsoft.azurebatch.jenkins.projectconfig.autogen.TestConfigs;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.VmConfigs;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.ProjectConfig;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.Resources;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.AzureStorageBlob;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.LocalResource;
import com.microsoft.azurebatch.jenkins.resource.AzureBlobResourceEntity;
import com.microsoft.azurebatch.jenkins.resource.LocalResourceEntity;
import com.microsoft.azurebatch.jenkins.resource.ResourceEntity;
import hudson.model.BuildListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectConfigHelper class
 */
public class ProjectConfigHelper 
{
    private ProjectConfig projectConfig = null;
    
    /**
     * ProjectConfigHelper constructor
     * @param listener BuildListener
     * @param projectConfigFile project config file path
     * @throws IOException
     */
    public ProjectConfigHelper(BuildListener listener, String projectConfigFile) throws IOException 
    {
        projectConfig = ProjectConfigFactory.generateProjectConfig(listener, projectConfigFile);
    }

    private ProjectConfig getProjectConfig() {
        return projectConfig;
    }
    
    /**
     * Add local resources to sharedResourceEntityList
     * @param workspacePath workspace path
     * @param sharedResourceEntityList shared ResourceEntityList
     * @throws FileNotFoundException
     */
    public void addLocalResources(String workspacePath, List<ResourceEntity> sharedResourceEntityList)
            throws FileNotFoundException
    {
        for(LocalResource localResource : this.getProjectConfig().getResources().getLocalResources())
        {
            String resourcePath = localResource.getSource();
            File path = new File(resourcePath);
            
            if (!path.exists())
            {
                // append to make full path  
                Path fullPath = Paths.get(workspacePath, resourcePath);
                resourcePath = fullPath.toString();              
            }
            
            LocalResourceEntity res = new LocalResourceEntity(resourcePath);
            sharedResourceEntityList.add(res);            
        }        
    }
    
    /**
     * Add blob resources to sharedResourceEntityList
     * @param sharedResourceEntityList shared ResourceEntityList
     * @throws FileNotFoundException
     */
    public void addBlobResources(List<ResourceEntity> sharedResourceEntityList)
            throws FileNotFoundException
    {
        for(AzureStorageBlob blobResource:this.getProjectConfig().getResources().getAzureStorageBlobs())            
        {
            String sas = blobResource.getSas();
            String blob = blobResource.getBlob();

            AzureBlobResourceEntity blobResourceEntity = 
                    new AzureBlobResourceEntity(blob, 
                            sas,
                            blobResource.isUnzip());
            sharedResourceEntityList.add(blobResourceEntity);
        }
    }
    
    /**
     * Get version of project config
     * @return version of project config
     */
    public String getVersion() {
        return getProjectConfig().getVersion();
    }
    
    /**
     * Get VM configs
     * @return VM configs
     */
    public VmConfigs getVMConfigs() {
        return getProjectConfig().getVmConfigs();
    }
    
    /**
     * Get resources
     * @return resources
     */
    public Resources getResources() {
        return getProjectConfig().getResources();
    }
    
    /**
     * Get TestConfigs
     * @return TestConfigs
     */
    public TestConfigs getTestConfigs() {
        return getProjectConfig().getTestConfigs();
    }        
}
