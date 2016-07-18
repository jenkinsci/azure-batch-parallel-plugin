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
 * Parallel testing plugin project config schema.
 * <p>
 * Parallel testing Jenkins plugin with Microsoft Azure Batch service. This schema defines the project configs for this plugin.
 * 
 */
@Generated("org.jsonschema2pojo")
public class ProjectConfig {

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     */
    @SerializedName("version")
    @Expose
    private String version;
    /**
     * VmConfigs schema.
     * <p>
     * Configs for task VMs. In current version, only Windows VM may be allocated.
     * (Required)
     * 
     */
    @SerializedName("vmConfigs")
    @Expose
    private VmConfigs vmConfigs;
    /**
     * Resources schema.
     * <p>
     * Resources folders and files will be copied to task VMs under folder %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID% before any test running.
     * 
     */
    @SerializedName("resources")
    @Expose
    private Resources resources;
    /**
     * TestConfigs schema.
     * <p>
     * Test related configs.
     * 
     */
    @SerializedName("testConfigs")
    @Expose
    private TestConfigs testConfigs;

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     * @return
     *     The version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Version schema.
     * <p>
     * Version of this project config schema, current version is 0.1.
     * (Required)
     * 
     * @param version
     *     The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * VmConfigs schema.
     * <p>
     * Configs for task VMs. In current version, only Windows VM may be allocated.
     * (Required)
     * 
     * @return
     *     The vmConfigs
     */
    public VmConfigs getVmConfigs() {
        return vmConfigs;
    }

    /**
     * VmConfigs schema.
     * <p>
     * Configs for task VMs. In current version, only Windows VM may be allocated.
     * (Required)
     * 
     * @param vmConfigs
     *     The vmConfigs
     */
    public void setVmConfigs(VmConfigs vmConfigs) {
        this.vmConfigs = vmConfigs;
    }

    /**
     * Resources schema.
     * <p>
     * Resources folders and files will be copied to task VMs under folder %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID% before any test running.
     * 
     * @return
     *     The resources
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * Resources schema.
     * <p>
     * Resources folders and files will be copied to task VMs under folder %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID% before any test running.
     * 
     * @param resources
     *     The resources
     */
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    /**
     * TestConfigs schema.
     * <p>
     * Test related configs.
     * 
     * @return
     *     The testConfigs
     */
    public TestConfigs getTestConfigs() {
        return testConfigs;
    }

    /**
     * TestConfigs schema.
     * <p>
     * Test related configs.
     * 
     * @param testConfigs
     *     The testConfigs
     */
    public void setTestConfigs(TestConfigs testConfigs) {
        this.testConfigs = testConfigs;
    }

}
