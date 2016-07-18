/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.projectconfig.autogen;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * TestConfigs schema.
 * <p>
 * Test related configs.
 * 
 */
@Generated("org.jsonschema2pojo")
public class TestConfigs {

    /**
     * ResultFilePatterns schema.
     * <p>
     * Patterns of result files which will be downloaded to Jenkins server.
     * 
     */
    @SerializedName("resultFilePatterns")
    @Expose
    private List<String> resultFilePatterns = new ArrayList<String>();
    /**
     * ResultFilesSaveToFolder schema.
     * <p>
     * The folder name of results files to be downloaded to Jenkins server.
     * 
     */
    @SerializedName("resultFilesSaveToFolder")
    @Expose
    private String resultFilesSaveToFolder = "azurebatchtemp\\results";

    /**
     * ResultFilePatterns schema.
     * <p>
     * Patterns of result files which will be downloaded to Jenkins server.
     * 
     * @return
     *     The resultFilePatterns
     */
    public List<String> getResultFilePatterns() {
        return resultFilePatterns;
    }

    /**
     * ResultFilePatterns schema.
     * <p>
     * Patterns of result files which will be downloaded to Jenkins server.
     * 
     * @param resultFilePatterns
     *     The resultFilePatterns
     */
    public void setResultFilePatterns(List<String> resultFilePatterns) {
        this.resultFilePatterns = resultFilePatterns;
    }

    /**
     * ResultFilesSaveToFolder schema.
     * <p>
     * The folder name of results files to be downloaded to Jenkins server.
     * 
     * @return
     *     The resultFilesSaveToFolder
     */
    public String getResultFilesSaveToFolder() {
        return resultFilesSaveToFolder;
    }

    /**
     * ResultFilesSaveToFolder schema.
     * <p>
     * The folder name of results files to be downloaded to Jenkins server.
     * 
     * @param resultFilesSaveToFolder
     *     The resultFilesSaveToFolder
     */
    public void setResultFilesSaveToFolder(String resultFilesSaveToFolder) {
        this.resultFilesSaveToFolder = resultFilesSaveToFolder;
    }

}
