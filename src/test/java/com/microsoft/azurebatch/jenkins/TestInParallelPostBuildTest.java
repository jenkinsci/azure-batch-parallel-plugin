package com.microsoft.azurebatch.jenkinstest;

import com.microsoft.azurebatch.jenkins.TestInParallelPostBuild;
import com.microsoft.azurebatch.jenkins.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class TestInParallelPostBuildTest {

    //Making sure that Validation works as expected
    @Test
    public void SimplestUnitTest()
    {
        TestInParallelPostBuild t = new TestInParallelPostBuild("a", "b", "c", "d", "e", "f");
        URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
        t.setDefinitionFilePath(location.getFile());
        Assert.assertEquals(true, t.Validation(null));
    }

    @Test
    public void GetAbsolutePathTest()
    {
        TestInParallelPostBuild t = new TestInParallelPostBuild("a", "b", "c", "d", "e", "f");
        System.out.println(t.getWorkspacePath());
        Assert.assertNotEquals(null, t.GetAbsolutePath("a"));
    }

    @Test
    public void TestDefinitionFileParsing1()
    {
        UpdateTaskGroupDefinitionMapTest(".\\src\\test\\resources\\test1.xml", true );
        UpdateTaskGroupDefinitionMapTest(".\\src\\test\\resources\\test2.xml", false);
    }

    private void UpdateTaskGroupDefinitionMapTest(String filePath, boolean expectedValue)
    {
        TestInParallelPostBuild t = new TestInParallelPostBuild("a", "b", "c", "d", "e", "f");
        t.setDefinitionFilePath(filePath);
        Assert.assertEquals(expectedValue, t.UpdateTaskGroupDefinitionMap(null, null));
    }


}