/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azurebatch.jenkins.logger;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import hudson.model.TaskListener;

/**
 * Logger helper class.
 */
public class Logger {
    /**
     * Log message to listener.
     * @param listener BuildListener
     * @param pattern message pattern
     * @param objects message details
     */
    public static void log(TaskListener listener, String pattern, Object ... objects) {
        log(listener, String.format(pattern, objects));
    }

    /**
     * Log exception to listener.
     * @param listener BuildListener
     * @param e the exception
     */
    public static void log(TaskListener listener, Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter, false)) {
            e.printStackTrace(printWriter);
        }

        log(listener, getDateTime(false) + "***EXCEPTION: " + e.getMessage());
        log(listener, getDateTime(false) + stringWriter.getBuffer().toString());
    }

    private static void log(TaskListener listener, String message)
    {
        if (listener != null) {
            listener.getLogger().println(getDateTime(false) + message);    
        }        
    }

    private static String getDateTime(boolean fShowTimeZone)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date) + (fShowTimeZone ?  " " + Calendar.getInstance().getTimeZone().getDisplayName() : "") + " ";
    }
}