/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.azurebatch.jobgen;

/**
 * Class to create JobGenerator
 */
public class JobGeneratorFactory {
    public static JobGenerator CreateJobGenerator(boolean isJobRunningOnWindows) {
        if (isJobRunningOnWindows) {
            return new JobGeneratorWindows();
        } else {
            return new JobGeneratorLinux();
        }
    }
}
