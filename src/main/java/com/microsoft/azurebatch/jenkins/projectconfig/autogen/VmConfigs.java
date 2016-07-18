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
     * OsFamily schema.
     * <p>
     * The Azure Guest OS family to be installed on the virtual machines in the pool. The default value is 4 – OS Family 4, equivalent to Windows Server 2012 R2. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, osFamily element.
     * 
     */
    @SerializedName("osFamily")
    @Expose
    private String osFamily = "4";
    /**
     * TargetOSVersion schema.
     * <p>
     * The Azure Guest OS version to be installed on the virtual machines in the pool. The default value is * which specifies the latest operating system version for the specified family. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, targetOSVersion element.
     * 
     */
    @SerializedName("targetOSVersion")
    @Expose
    private String targetOSVersion = "*";
    /**
     * VmSize schema.
     * <p>
     * The size of the virtual machine. Default vmSize is small. You may find more information at https://azure.microsoft.com/documentation/articles/cloud-services-sizes-specs/ (ExtraSmall is not supported).
     * 
     */
    @SerializedName("vmSize")
    @Expose
    private String vmSize = "small";
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
     * OsFamily schema.
     * <p>
     * The Azure Guest OS family to be installed on the virtual machines in the pool. The default value is 4 – OS Family 4, equivalent to Windows Server 2012 R2. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, osFamily element.
     * 
     * @return
     *     The osFamily
     */
    public String getOsFamily() {
        return osFamily;
    }

    /**
     * OsFamily schema.
     * <p>
     * The Azure Guest OS family to be installed on the virtual machines in the pool. The default value is 4 – OS Family 4, equivalent to Windows Server 2012 R2. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, osFamily element.
     * 
     * @param osFamily
     *     The osFamily
     */
    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily;
    }

    /**
     * TargetOSVersion schema.
     * <p>
     * The Azure Guest OS version to be installed on the virtual machines in the pool. The default value is * which specifies the latest operating system version for the specified family. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, targetOSVersion element.
     * 
     * @return
     *     The targetOSVersion
     */
    public String getTargetOSVersion() {
        return targetOSVersion;
    }

    /**
     * TargetOSVersion schema.
     * <p>
     * The Azure Guest OS version to be installed on the virtual machines in the pool. The default value is * which specifies the latest operating system version for the specified family. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section, targetOSVersion element.
     * 
     * @param targetOSVersion
     *     The targetOSVersion
     */
    public void setTargetOSVersion(String targetOSVersion) {
        this.targetOSVersion = targetOSVersion;
    }

    /**
     * VmSize schema.
     * <p>
     * The size of the virtual machine. Default vmSize is small. You may find more all available vmSize at https://azure.microsoft.com/documentation/articles/cloud-services-sizes-specs/ (ExtraSmall is not supported).
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
     * The size of the virtual machine. Default vmSize is small. You may find more all available vmSize at https://azure.microsoft.com/documentation/articles/cloud-services-sizes-specs/ (ExtraSmall is not supported).
     * 
     * @param vmSize
     *     The vmSize
     */
    public void setVmSize(String vmSize) {
        this.vmSize = vmSize;
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
