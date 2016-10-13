/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig.autogen;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * VmConfigs schema.
 * <p>
 * Configs for task VMs. In current version, only Windows VM may be allocated.
 * 
 */
@Generated("org.jsonschema2pojo")
public class VmConfigs {

    /**
     * NumVMs schema.
     * <p>
     * Number of VMs to be created.
     * (Required)
     * 
     */
    @SerializedName("numVMs")
    @Expose
    private int numVMs;
    /**
     * MaxTasksPerNode schema.
     * <p>
     * Max tasks allowed running on a VM at same time.
     * 
     */
    @SerializedName("maxTasksPerNode")
    @Expose
    private int maxTasksPerNode = 1;
    /**
     * VmSize schema.
     * <p>
     * The size of the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, element vmSize.
     * 
     */
    @SerializedName("vmSize")
    @Expose
    private String vmSize;
    /**
     * CloudServiceConfig schema.
     * <p>
     * The cloud service configuration for the pool, for Azure PaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section.
     * 
     */
    @SerializedName("cloudServiceConfig")
    @Expose
    private CloudServiceConfig cloudServiceConfig;
    /**
     * VirtualMachineConfig schema.
     * <p>
     * The virtual machine configuration for the pool, for Azure IaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section.
     * 
     */
    @SerializedName("virtualMachineConfig")
    @Expose
    private VirtualMachineConfig virtualMachineConfig;
    /**
     * PoolKeepAlive schema.
     * <p>
     * Keep the VMs alive after all tests are done. Default is false. You may set to true if you want VMs alive for debugging purpose.
     * 
     */
    @SerializedName("poolKeepAlive")
    @Expose
    private boolean poolKeepAlive = false;
    /**
     * VmSetupCommandLine schema.
     * <p>
     * CommandLine to setup VM. This command will be run before any tests.
     * 
     */
    @SerializedName("vmSetupCommandLine")
    @Expose
    private String vmSetupCommandLine = "";

    /**
     * NumVMs schema.
     * <p>
     * Number of VMs to be created.
     * (Required)
     * 
     * @return
     *     The numVMs
     */
    public int getNumVMs() {
        return numVMs;
    }

    /**
     * NumVMs schema.
     * <p>
     * Number of VMs to be created.
     * (Required)
     * 
     * @param numVMs
     *     The numVMs
     */
    public void setNumVMs(int numVMs) {
        this.numVMs = numVMs;
    }

    /**
     * MaxTasksPerNode schema.
     * <p>
     * Max tasks allowed running on a VM at same time.
     * 
     * @return
     *     The maxTasksPerNode
     */
    public int getMaxTasksPerNode() {
        return maxTasksPerNode;
    }

    /**
     * MaxTasksPerNode schema.
     * <p>
     * Max tasks allowed running on a VM at same time.
     * 
     * @param maxTasksPerNode
     *     The maxTasksPerNode
     */
    public void setMaxTasksPerNode(int maxTasksPerNode) {
        this.maxTasksPerNode = maxTasksPerNode;
    }

    /**
     * VmSize schema.
     * <p>
     * The size of the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, element vmSize.
     * 
     * @return
     *     The vmSize
     */
    public String getVmSize() {
        return vmSize;
    }

    /**
     * VmSize schema.
     * <p>
     * The size of the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, element vmSize.
     * 
     * @param vmSize
     *     The vmSize
     */
    public void setVmSize(String vmSize) {
        this.vmSize = vmSize;
    }

    /**
     * CloudServiceConfig schema.
     * <p>
     * The cloud service configuration for the pool, for Azure PaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section.
     * 
     * @return
     *     The cloudServiceConfig
     */
    public CloudServiceConfig getCloudServiceConfig() {
        return cloudServiceConfig;
    }

    /**
     * CloudServiceConfig schema.
     * <p>
     * The cloud service configuration for the pool, for Azure PaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section.
     * 
     * @param cloudServiceConfig
     *     The cloudServiceConfig
     */
    public void setCloudServiceConfig(CloudServiceConfig cloudServiceConfig) {
        this.cloudServiceConfig = cloudServiceConfig;
    }

    /**
     * VirtualMachineConfig schema.
     * <p>
     * The virtual machine configuration for the pool, for Azure IaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section.
     * 
     * @return
     *     The virtualMachineConfig
     */
    public VirtualMachineConfig getVirtualMachineConfig() {
        return virtualMachineConfig;
    }

    /**
     * VirtualMachineConfig schema.
     * <p>
     * The virtual machine configuration for the pool, for Azure IaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section.
     * 
     * @param virtualMachineConfig
     *     The virtualMachineConfig
     */
    public void setVirtualMachineConfig(VirtualMachineConfig virtualMachineConfig) {
        this.virtualMachineConfig = virtualMachineConfig;
    }

    /**
     * PoolKeepAlive schema.
     * <p>
     * Keep the VMs alive after all tests are done. Default is false. You may set to true if you want VMs alive for debugging purpose.
     * 
     * @return
     *     The poolKeepAlive
     */
    public boolean isPoolKeepAlive() {
        return poolKeepAlive;
    }

    /**
     * PoolKeepAlive schema.
     * <p>
     * Keep the VMs alive after all tests are done. Default is false. You may set to true if you want VMs alive for debugging purpose.
     * 
     * @param poolKeepAlive
     *     The poolKeepAlive
     */
    public void setPoolKeepAlive(boolean poolKeepAlive) {
        this.poolKeepAlive = poolKeepAlive;
    }

    /**
     * VmSetupCommandLine schema.
     * <p>
     * CommandLine to setup VM. This command will be run before any tests.
     * 
     * @return
     *     The vmSetupCommandLine
     */
    public String getVmSetupCommandLine() {
        return vmSetupCommandLine;
    }

    /**
     * VmSetupCommandLine schema.
     * <p>
     * CommandLine to setup VM. This command will be run before any tests.
     * 
     * @param vmSetupCommandLine
     *     The vmSetupCommandLine
     */
    public void setVmSetupCommandLine(String vmSetupCommandLine) {
        this.vmSetupCommandLine = vmSetupCommandLine;
    }

}
