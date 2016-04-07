package com.microsoft.azurebatch.jenkins;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import hudson.model.TaskListener;

/**
 * Created by krkotipa on 3/9/2016.
 */
public class Logger {
    public static void Log(TaskListener listener, String pattern, Object ... objects) {
        Log(listener, String.format(pattern, objects));
    }

    public static void Log(TaskListener listener, Exception e)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, false);
        e.printStackTrace(printWriter);
        printWriter.close();

        Log(listener, getDateTime(false) + "***EXCEPTION: " + e.getMessage());
        Log(listener, getDateTime(false) + stringWriter.getBuffer().toString());
    }

    public static void Log(TaskListener listener, String message)
    {
        listener.getLogger().println(getDateTime(false) + message);
    }

    private static String getDateTime(boolean fShowTimeZone)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date) + (fShowTimeZone ?  " " + Calendar.getInstance().getTimeZone().getDisplayName() : "") + " ";
    }
}