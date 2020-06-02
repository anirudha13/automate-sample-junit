package my.anirudha.samples;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class SampleSeleniumTest {

    private static final Logger LOGGER = LogManager.getLogger(SampleSeleniumTest.class);
    private static final String SCREENSHOT_FILE_PATH = "live-dashboard.png";

    private static final String USERNAME = "anirudhakhanna5";
    private static final String AUTOMATE_KEY = "yUGx7w2dij48Wfg6cRQb";
    private static final String REMOTE_HUB_URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @Test
    @Ignore
    public void testBrowserStackLoginOnLocal() throws Exception {
        /* =================== Prepare ================= */
        System.setProperty("webdriver.chrome.driver", "/Users/anirudha/bin/chromedriver");
        WebDriver webDriver = new ChromeDriver();

        /* =================== Execute & Verify ================= */
        runTest(webDriver);
    }

    @Test
    public void testBrowserStackLoginOnHub() throws Exception {
        /* =================== Prepare ================= */
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "83.0");

        Map<String, Object> browserstackOptions = new HashMap<String, Object>();
        browserstackOptions.put("os", "OS X");
        browserstackOptions.put("osVersion", "Catalina");
        browserstackOptions.put("projectName", "Onboarding Samples");
        browserstackOptions.put("buildName", "JUnit-Selenium");
        browserstackOptions.put("sessionName", "Browserstack-Login");
        browserstackOptions.put("local", "false");
        browserstackOptions.put("consoleLogs", "info");
        browserstackOptions.put("networkLogs", "true");
        capabilities.setCapability("bstack:options", browserstackOptions);


        WebDriver webDriver = new RemoteWebDriver(new URL(REMOTE_HUB_URL), capabilities);

        /* =================== Execute & Verify ================= */
        runTest(webDriver);
    }

    private void runTest(WebDriver webDriver) throws IOException, InterruptedException {
        LOGGER.info("Navigating to BrowserStack home page");
        webDriver.get("https://www.browserstack.com/");
        WebElement signInElement = webDriver.findElement(By.linkText("Sign in"));
        Assert.assertNotNull("No Sign In button found", signInElement);
        signInElement.click();

        LOGGER.info("Accepting Cookie Notifications");
        WebElement acceptCookieButton = webDriver.findElement(By.id("accept-cookie-notification"));
        acceptCookieButton.click();

        LOGGER.info("Starting Sign In actions");
        WebElement emailElement = webDriver.findElement(By.id("user_email_login"));
        WebElement passwordElement = webDriver.findElement(By.id("user_password"));
        WebElement submitElement = webDriver.findElement(By.id("user_submit"));

        emailElement.sendKeys("anirudha.khanna@gmail.com");
        Thread.sleep(100);
        passwordElement.sendKeys("Automate@123");
        Thread.sleep(100);
        submitElement.click();

        LOGGER.info("Successfully signed in to BrowserStack Live dashboard...waiting for dashboard to load");

        /* =================== Verify ================= */
        Thread.sleep(10000);
        WebElement dashboardSection = webDriver.findElement(By.id("rf-browser-list-wrapper"));
        Assert.assertNotNull("Live Dashboard is not available", dashboardSection);
        LOGGER.info("Taking screenshot of Live dashboard");

        //Call getScreenshotAs method to create image file
        File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        //Move image file to new destination
        File destFile = new File(SCREENSHOT_FILE_PATH);
        //Copy file at destination
        FileUtils.copyFile(srcFile, destFile);

        LOGGER.info("Completed Live Dashboard test");
        webDriver.quit();
    }
}
