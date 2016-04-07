package com.microsoft.azurebatch.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.microsoft.azurebatch.jenkins.Utils.getDateTime;

/**
 * Created by nirajg on 3/18/2016.
 */
public class Utils {

    public static enum TraceType {
        Error,
        Status,
        Debug,
        Empty,
    }

    public static void print(BuildListener listener, String message) {
        if(listener != null) {
            print(listener, message, TraceType.Empty);
        }
        else
        {
            System.out.println(message);
        }
    }

    public static void print(BuildListener listener, String message, TraceType traceType) {
        String traceTypeString = "";

        if (traceType != TraceType.Empty) {
            traceTypeString = traceType.toString();
        }

        /*
        listener.getLogger().println(
                String.format("%s[%s]: %s",
                        traceTypeString,
                        getDateTime(false),
                        message)
        );*/
        if(listener != null) {
            listener.getLogger().println(
                    String.format("%s", message)
            );
        }
        else
        {
            // Mainly for test
            System.out.println(message);
        }
    }

    public static String getDateTime(boolean fShowTimeZone) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date) + (fShowTimeZone ? " " + Calendar.getInstance().getTimeZone().getDisplayName() : "") + " ";
    }

    public static void CompressResourceFiles(AbstractBuild<?, ?> build,
                                             BuildListener listener,
                                             String compressionToolFilePath,
                                             String compressedDropDirectory,
                                             String compressedFileName,
                                             String filesOrDirToBeCompressed) {
        print(listener, "compressionToolFilePath:" + compressionToolFilePath);
        if (!fileExists(compressionToolFilePath)) {
            print(listener, String.format("%s doesn't exist", compressionToolFilePath), TraceType.Error);
            build.setResult(Result.FAILURE);
        }

        try {
            if (compressedDropDirectory.endsWith("\"")) {
                compressedDropDirectory = compressedDropDirectory.substring(0, compressedDropDirectory.length() - 1);
            }

            String compressFile = compressedDropDirectory + "\\" + compressedFileName + "\"";

            if (!compressedDropDirectory.startsWith("\"")) {
                compressFile = "\"" + compressFile;
            }

            String compressionCommandLine = String.format("%s a %s %s", compressionToolFilePath, compressFile, filesOrDirToBeCompressed);
            print(listener, "compressionCommandLine:" + compressionCommandLine);
            ProcessBuilder builder = new ProcessBuilder(compressionCommandLine);
            builder.redirectErrorStream(true);
            Process p = builder.start();


            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                print(listener, line, TraceType.Debug);
            }
            r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                print(listener, line, TraceType.Debug);
            }


        } catch (IOException e) {
            print(listener, e.getMessage());
        }

    }

    public static void deleteDirectoryIncludeContent(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteDirectoryIncludeContent(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete : " + f);
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean dirExists(String path) {
        File f = new File(path);
        if (f.isDirectory() && f.exists()) {
            return true;
        }
        return false;
    }

}
