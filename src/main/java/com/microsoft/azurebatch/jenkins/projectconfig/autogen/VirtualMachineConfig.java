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
 * VirtualMachineConfig schema.
 * <p>
 * The virtual machine configuration for the pool, for Azure IaaS VMs. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section.
 * 
 */
@Generated("org.jsonschema2pojo")
public class VirtualMachineConfig {

    /**
     * Publisher schema.
     * <p>
     * The publisher of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, publisher element.
     * (Required)
     * 
     */
    @SerializedName("publisher")
    @Expose
    private String publisher;
    /**
     * Offer schema.
     * <p>
     * The offer of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, offer element.
     * (Required)
     * 
     */
    @SerializedName("offer")
    @Expose
    private String offer;
    /**
     * Sku schema.
     * <p>
     * The SKU of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, sku element.
     * (Required)
     * 
     */
    @SerializedName("sku")
    @Expose
    private String sku;
    /**
     * Version schema.
     * <p>
     * The version of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, version element.
     * 
     */
    @SerializedName("version")
    @Expose
    private String version = "latest";
    /**cd
     * NodeAgentSKUId schema.
     * <p>
     * The SKU of the Batch node agent that needs to be provisioned on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section, nodeAgentSKUId element.
     * (Required)
     * 
     */
    @SerializedName("nodeAgentSKUId")
    @Expose
    private String nodeAgentSKUId;

    /**
     * Publisher schema.
     * <p>
     * The publisher of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, publisher element.
     * (Required)
     * 
     * @return
     *     The publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Publisher schema.
     * <p>
     * The publisher of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, publisher element.
     * (Required)
     * 
     * @param publisher
     *     The publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Offer schema.
     * <p>
     * The offer of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, offer element.
     * (Required)
     * 
     * @return
     *     The offer
     */
    public String getOffer() {
        return offer;
    }

    /**
     * Offer schema.
     * <p>
     * The offer of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, offer element.
     * (Required)
     * 
     * @param offer
     *     The offer
     */
    public void setOffer(String offer) {
        this.offer = offer;
    }

    /**
     * Sku schema.
     * <p>
     * The SKU of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, sku element.
     * (Required)
     * 
     * @return
     *     The sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sku schema.
     * <p>
     * The SKU of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, sku element.
     * (Required)
     * 
     * @param sku
     *     The sku
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Version schema.
     * <p>
     * The version of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, version element.
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
     * The version of the image to be installed on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, imageReference section, version element.
     * 
     * @param version
     *     The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * NodeAgentSKUId schema.
     * <p>
     * The SKU of the Batch node agent that needs to be provisioned on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section, nodeAgentSKUId element.
     * (Required)
     * 
     * @return
     *     The nodeAgentSKUId
     */
    public String getNodeAgentSKUId() {
        return nodeAgentSKUId;
    }

    /**
     * NodeAgentSKUId schema.
     * <p>
     * The SKU of the Batch node agent that needs to be provisioned on the virtual machines in the pool. You may find more information at https://msdn.microsoft.com/library/azure/dn820174.aspx, virtualMachineConfiguration section, nodeAgentSKUId element.
     * (Required)
     * 
     * @param nodeAgentSKUId
     *     The nodeAgentSKUId
     */
    public void setNodeAgentSKUId(String nodeAgentSKUId) {
        this.nodeAgentSKUId = nodeAgentSKUId;
    }

}
