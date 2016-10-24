/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig;

import com.google.gson.Gson;
import com.microsoft.azurebatch.jenkins.logger.Logger;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.CloudServiceConfig;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.ProjectConfig;
import com.microsoft.azurebatch.jenkins.projectconfig.autogen.VirtualMachineConfig;
import com.microsoft.azurebatch.jenkins.utils.Utils;
import hudson.model.BuildListener;

import java.io.*;
import java.nio.charset.Charset;

/**
 * ProjectConfigFactory class
 */
public class ProjectConfigFactory {

    /**
     * Generate project config
     * @param listener BuildListener
     * @param fullFilePath full file path of project config file
     * @return ProjectConfig instance
     * @throws IOException
     */
    public static ProjectConfig generateProjectConfig(BuildListener listener, String fullFilePath) throws IOException
    {              
        Logger.log(listener, "Reading project configurations from %s...", fullFilePath);
                
        if (!Utils.fileExists(fullFilePath)) {
            throw new IOException(String.format("Project config file '%s' doesn't exist, please double check your configuration.", fullFilePath));
        }
        
        Gson gson = new Gson();
        ProjectConfig config = null;
        try (Reader reader = new InputStreamReader(new FileInputStream(new File(fullFilePath)), Charset.defaultCharset())) {
            config = gson.fromJson(reader, ProjectConfig.class);
        }
        
        // TODO: validate against schema
        // Do some basic check for project config, in case customer may provide wrong config file.
        basicCheckProjectConfig(listener, config);
                
        Logger.log(listener, "Created project config from config %s", fullFilePath);
        return config;
    }

    private static void basicCheckProjectConfig(BuildListener listener, ProjectConfig config) throws InvalidObjectException {
        if (config.getVersion() == null || config.getVersion().isEmpty()) {
            throw new InvalidObjectException("version is not set in project config file, please double check your configuration.");
        }
        
        if (config.getVmConfigs() == null) {
            throw new InvalidObjectException("vmConfigs section is not configured in project config file, please double check your configuration.");
        }
        
        if (config.getVmConfigs().getNumVMs() < 1) {
            throw new InvalidObjectException("numVMs is not set in project config file, please double check your configuration.");
        }
        
        if (config.getVmConfigs().getVmSize() == null || config.getVmConfigs().getVmSize().isEmpty()) {
            throw new InvalidObjectException("vmSize is not set in project config file, please double check your configuration.");
        }
        
        VirtualMachineConfig virtualMachineConfig = config.getVmConfigs().getVirtualMachineConfig();
        CloudServiceConfig cloudServiceConfig = config.getVmConfigs().getCloudServiceConfig();
        
        if (virtualMachineConfig != null && cloudServiceConfig != null) {
            throw new InvalidObjectException("Both cloud service config and virtual machine config are set in project config file, please double check your configuration.");
        }
        
        if (virtualMachineConfig != null) {
            if (virtualMachineConfig.getPublisher() == null || virtualMachineConfig.getPublisher().isEmpty()) {
                throw new InvalidObjectException("publisher for virtualMachineConfig is not set in project config file, please double check your configuration.");
            }
            
            if (virtualMachineConfig.getOffer() == null || virtualMachineConfig.getOffer().isEmpty()) {
                throw new InvalidObjectException("offer for virtualMachineConfig is not set in project config file, please double check your configuration.");
            }
            
            if (virtualMachineConfig.getSku()== null || virtualMachineConfig.getSku().isEmpty()) {
                throw new InvalidObjectException("sku for virtualMachineConfig is not set in project config file, please double check your configuration.");
            }
            
            if (virtualMachineConfig.getNodeAgentSKUId()== null || virtualMachineConfig.getNodeAgentSKUId().isEmpty()) {
                throw new InvalidObjectException("nodeAgentSKUId for virtualMachineConfig is not set in project config file, please double check your configuration.");
            }
        } else if (cloudServiceConfig == null) {
            // If project config doesn't specify CloudServiceConfig and VirtualMachineConfig,
            // let's create a default CloudServiceConfig
            config.getVmConfigs().setCloudServiceConfig(new CloudServiceConfig());
            Logger.log(listener, "Project config doesn't specify CloudServiceConfig and VirtualMachineConfig, will use default CloudServiceConfig.");
        }
    }      
}
