/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.azurebatch;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Batch account info class
 */
public class BatchAccountInfo {

    // Azure Batch account friendly name.
    private String friendlyName;
    
    // Azure Batch account name.
    private String accountName;

    // Azure Batch account primary access key.
    private String accountKey;

    // Azure Batch Service url.
    private String serviceURL;

    /**
     * BatchAccountInfo constructor
     * @param friendlyName friendly name
     * @param accountName account name
     * @param accountKey account key
     * @param serviceURL service URL
     */
    @DataBoundConstructor
    public BatchAccountInfo(final String friendlyName, final String accountName, final String accountKey,
                              final String serviceURL) {
        this.friendlyName = friendlyName;
        this.accountName = accountName;
        this.accountKey	= accountKey;
        this.serviceURL = serviceURL;
    }

    /**
     * Get friendly name
     * @return friendly name
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Set friendly name
     * @param friendlyName friendly name
     */
    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Get account name
     * @return account name
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Set account name
     * @param accountName account name
     */
    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    /**
     * Get account key
     * @return account key
     */
    public String getAccountKey() {
        return accountKey;
    }

    /**
     * Set account key
     * @param accountKey account key
     */
    public void setAccountKey(final String accountKey) {
        this.accountKey = accountKey;
    }

    /**
     * Get service URL
     * @return service URL
     */
    public String getServiceURL() {
        return serviceURL;
    }

    /**
     * Set service URL
     * @param serviceURL service URL
     */
    public void setServiceURL(final String serviceURL) {
        this.serviceURL = serviceURL;
    }
}

