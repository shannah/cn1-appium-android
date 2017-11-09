/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.tests.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;
/**
 *
 * @author shannah
 */
@SuppressWarnings("deprecation")
public class CN1AppiumAndroidTest {
     private AppiumDriver<WebElement> driver;

    private List<Integer> values;

    private static final int MINIMUM = 0;
    private static final int MAXIMUM = 10;
    private long startTime = 0;

    @Before
    public void setUp() throws Exception {
        
        System.out.println("Codename One Appium Unit Tester:");
        System.out.println("---------------------------------");
        System.out.println("The following properties are required:");
        System.out.println("  -Dapp=/path/to/app.apk  : The path to your apk to test");
        System.out.println("  -DdeviceName=[Device Name to Run On]\n");
        System.out.println("Example (Running on Emulator): ");
        System.out.println("  $ mvn test -Dapp=./MyApp.apk -DdeviceName=emulator-5554\n");
        System.out.println("Example (Running on Device):");
        System.out.println("  $ mvn test -Dapp=./MyApp.apk -DdeviceName=04353c3213b7092d\n");
        System.out.println("\nYou can obtain the list of running emulators and connected devices by running:");
        System.out.println("  $ adb devices -l");
        
        // set up appium
        //File appDir = new File(System.getProperty("user.dir"), "../../../apps/TestApp/build/release-iphonesimulator");
        //File app = new File("/var/folders/k7/b5qdhxt88v58wp008k8yxy180000gn/T/build4495066499899166821xxx/dist/build/Build/Products/Debug-iphonesimulator/CodenameOneUnitTestExecutor.app");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String apkPath = System.getProperty("app", null);
        if (apkPath != null) {
            capabilities.setCapability("app", apkPath);
            if (System.getProperty("deviceName") != null) {
                capabilities.setCapability("deviceName", System.getProperty("deviceName", "Android Emulator"));
            }
            if (System.getProperty("platformVersion") != null) {
                capabilities.setCapability("platformVersion", System.getProperty("platformVersion", "7.1.1"));
            }
        }
        // Device Farm doesn't need any device capabilities
        // It will set them on the server-side 
        /*
        capabilities.setCapability("platformVersion", "9.3");
        capabilities.setCapability("deviceName", "iPhone 6");
        capabilities.setCapability("app", app.getAbsolutePath());
        capabilities.setCapability("automationName", "XCUITest");
        */
        startTime = System.currentTimeMillis();
        driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        values = new ArrayList<Integer>();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
    
    private LogEntries getLogEntries(String logType) {
        try {
            return driver.manage().logs().get(logType);
        } catch (Throwable t) {
            return null;
        }
    }
    
    @Test
    public void runTest() throws Exception {
        System.out.println("Running tests...");
        Pattern failedPattern = Pattern.compile(".*Passed: (\\d+) tests\\. Failed: (\\d+) tests\\..*");
                                                            //Total 1 tests passed
        Pattern allPassedPattern = Pattern.compile(".*Total (\\d+) tests passed.*");
        //Pattern pidRegex = Pattern.compile("\\b"+Pattern.quote(pid)+"\\b");
        boolean testCompleted = false;
        boolean testPassed = true;
        int passedTests = 0;
        int failedTests = 0;
        Set<String> availableLogTypes = new HashSet<String>(driver.manage().logs().getAvailableLogTypes());
        StringBuilder sb = new StringBuilder();
        outer: while (true) {
            Set<String> logTypes = new HashSet<String>(availableLogTypes);
            for (String logType : logTypes) {
                LogEntries entries = getLogEntries(logType);
                if (entries == null) {
                    availableLogTypes.remove(logType);
                    continue;
                }
                Iterator<LogEntry> it = entries.iterator();
                while (it.hasNext()) {
                    LogEntry e = it.next();
                    if (e.getTimestamp() < startTime) {
                        continue;
                    }
                    //System.out.println(e.getMessage());
                    sb.append(e.getMessage()).append("\n");
                    if (e.getMessage().contains("-----FINISHED TESTS-----")) {
                        break outer;
                    }
                    String line = e.getMessage().trim();
                    Matcher m = failedPattern.matcher(line);
                    Matcher m2 = allPassedPattern.matcher(line);
                    if (m.find()) {
                        testCompleted = true;
                        String numFailedStr = m.group(2);
                        if (Integer.parseInt(numFailedStr) > 0) {
                            testPassed = false;
                        }
                        passedTests += Integer.parseInt(m.group(1));
                        failedTests += Integer.parseInt(m.group(2));
                        System.out.println(line);
                    } else if (m2.find()) {
                        testCompleted = true;
                        passedTests += Integer.parseInt(m2.group(1));
                    }
                }
            }
        }
        
        Assert.assertEquals("PASSED: "+passedTests+" FAILED: "+failedTests+"\n"+sb.toString(), 0, failedTests);
        
    }
}
