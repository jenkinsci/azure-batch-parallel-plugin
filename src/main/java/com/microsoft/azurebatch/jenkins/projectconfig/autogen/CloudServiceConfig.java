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
 * CloudServiceConfig schema.
 * <p>
 * The cloud service configuration for the pool, for Azure PaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, cloudServiceConfiguration section.
 * 
 */
@Generated("org.jsonschema2pojo")
public class CloudServiceConfig {

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

}
