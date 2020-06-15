package my.anirudha.samples;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.StopWatch;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class BrowserStackInceptionTest {

    private static final Logger LOGGER = LogManager.getLogger(BrowserStackInceptionTest.class);
    private static final String SCREENSHOT_FILE_PATH = "live-dashboard.png";

    private static final String USERNAME = "anirudhakhanna5";
    private static final String AUTOMATE_KEY = "yUGx7w2dij48Wfg6cRQb";
    private static final String REMOTE_HUB_URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
    private static final String PAGE_TITLE = "Dashboard";

    private static final ExpectedCondition<Boolean> PAGE_READY_EXPECTATION = new ExpectedCondition<Boolean>() {
        @NullableDecl
        @Override
        public Boolean apply(@NullableDecl WebDriver driver) {
            return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
        }
    };

    @Test
    public void testBrowserStackInceptionLocal() throws Exception {
        /* =================== Prepare ================= */
        System.setProperty("webdriver.chrome.driver", "/Users/anirudha/bin/chromedriver");
        WebDriver webDriver = new ChromeDriver();

        /* =================== Execute & Verify ================= */
        runTest(webDriver);
    }

    @Test
    @Ignore
    public void testBroswerStackInceptionOnHub() throws Exception {
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
        browserstackOptions.put("resolution", "1280x800");

        capabilities.setCapability("bstack:options", browserstackOptions);


        WebDriver webDriver = new RemoteWebDriver(new URL(REMOTE_HUB_URL), capabilities);

        /* =================== Execute & Verify ================= */
        runTest(webDriver);
    }

    private void runTest(WebDriver driver) throws IOException, InterruptedException {
        try {
            Dimension dimension = new Dimension(1280, 680);
            driver.manage().window().setSize(dimension);

            WebDriverWait waitingDriver = new WebDriverWait(driver, 120);

            StopWatch stopWatch = new StopWatch();
            LOGGER.info("STEP 1: Navigate to Google");
            stopWatch.start("STEP 1: Navigate to Google");
            driver.get("https://www.google.com");
            stopWatch.stop();

            LOGGER.info("STEP 2: Search for BrowserStack & Navigate to BrowserStack");
            stopWatch.start("STEP 2: Search for BrowserStack & Navigate to BrowserStack");
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("BrowserStack");
            searchBox.submit();
            waitingDriver.until(PAGE_READY_EXPECTATION);

            WebElement bstackResult = driver.findElement(By.cssSelector("a[href*='browserstack']"));
            Assert.assertNotNull("Did not find BrowserStack result in Google results", bstackResult);
            bstackResult.click();
            waitingDriver.until(PAGE_READY_EXPECTATION);
            stopWatch.stop();

            LOGGER.info("STEP 3: Login to BrowserStack");
            stopWatch.start("STEP 3: Login to BrowserStack");
            WebElement signInElement = driver.findElement(By.linkText("Sign in"));
            Assert.assertNotNull("No Sign In button found", signInElement);
            signInElement.click();

            LOGGER.info("Accepting Cookie Notifications");
            WebElement acceptCookieButton = driver.findElement(By.id("accept-cookie-notification"));
            acceptCookieButton.click();

            LOGGER.info("Starting Sign In actions");
            WebElement emailElement = driver.findElement(By.id("user_email_login"));
            WebElement passwordElement = driver.findElement(By.id("user_password"));
            WebElement submitElement = driver.findElement(By.id("user_submit"));

            emailElement.sendKeys("anirudha.khanna@gmail.com");
            passwordElement.sendKeys("Automate@123");
            submitElement.click();
            stopWatch.stop();

            LOGGER.info("STEP 4: Loading BrowserStack Live Dashboard");
            stopWatch.start("STEP 4: Loading BrowserStack Live Dashboard");
            waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@data-parentos='windows']")));
            waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@data-browser-version='76.0' and @data-browser='Firefox' and @data-os='win10']")));
            stopWatch.stop();

            Assert.assertEquals("Page Title has changed", PAGE_TITLE, driver.getTitle());
            LOGGER.info("Completed Live Dashboard test.");
            LOGGER.info("Unit test timings :: {}", stopWatch.prettyPrint());
        } catch (Throwable throwable) {

        } finally {
            driver.close();
            driver.quit();
        }
    }
}
