package com.browserstack.samples;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.StopWatch;

import com.browserstack.driver.config.Browser;
import com.browserstack.driver.config.SampleParseConfiguration;
import com.browserstack.driver.config.WebDriverConfiguration;
import com.browserstack.test.rules.WebDriverProviderRule;
import com.browserstack.test.runner.ParallelParameterized;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
@RunWith(ParallelParameterized.class)

public class BrowserStackLiveDashboardTest {

    private static final Logger LOGGER = LogManager.getLogger(BrowserStackLiveDashboardTest.class);

    private static final String PAGE_TITLE = "Dashboard";
    private static final String BROWSER_CONFIGURATION = "browser-configuration.yml";

    private static final ExpectedCondition<Boolean> PAGE_READY_EXPECTATION = new ExpectedCondition<>() {
        @Override
        public Boolean apply(@NullableDecl WebDriver driver) {
            return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
        }
    };

    private static WebDriverConfiguration webDriverConfiguration;

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        URL resourceURL = SampleParseConfiguration.class.getClassLoader().getResource(BROWSER_CONFIGURATION);
        webDriverConfiguration = objectMapper.readValue(resourceURL, WebDriverConfiguration.class);
        LOGGER.debug("Web Driver Configuration :: {}", webDriverConfiguration);
        List<Object[]> returnData = new ArrayList<>();
        List<? extends Browser> browsers = webDriverConfiguration.getLocalBrowsers();
        if (webDriverConfiguration.getRunOnBrowserStack()) {
            browsers = webDriverConfiguration.getBrowserStackWebDriverConfig().getRemoteBrowsers();
        }

        browsers.forEach(b -> {
            returnData.add(new Object[]{b});
        });
        return returnData;
    }

    @Parameterized.Parameter(0)
    public Browser browser;

    @Rule
    public final TestName testName = new TestName();

    @Rule
    public final WebDriverProviderRule webDriverProvider = new WebDriverProviderRule();

    @Test
    @Ignore
    public void testBrowserStackLiveDashboardLogin() throws Exception {
        /* =================== Prepare ================= */
        WebDriver webDriver = webDriverProvider.getWebDriver(webDriverConfiguration, browser);

        /* =================== Execute & Verify ================= */
        LOGGER.debug("Running Test {} on Browser {} with WebDriver :: {}", webDriverProvider.getMethodName(),
                     browser.getName(), webDriver);
        runTest(webDriver);
    }

    private void runTest(WebDriver driver) throws IOException, InterruptedException {
        WebDriverWait waitingDriver = new WebDriverWait(driver, 120);

        StopWatch stopWatch = new StopWatch();
        LOGGER.info("STEP 1: Navigate to Google {}", testName.getMethodName());
        stopWatch.start("STEP 1: Navigate to Google");
        driver.get("https://www.google.com");
        stopWatch.stop();

        LOGGER.info("STEP 2: Search for BrowserStack & Navigate to BrowserStack {}", testName.getMethodName());
        stopWatch.start("STEP 2: Search for BrowserStack & Navigate to BrowserStack");
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("BrowserStack");
        searchBox.submit();
        waitingDriver.until(PAGE_READY_EXPECTATION);

        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, 'browserstack')]/h3")));
        driver.findElement(By.xpath("//a[contains(@href, 'browserstack')]/h3")).click();
        stopWatch.stop();

        LOGGER.info("STEP 3: Login to BrowserStack :: {}", testName.getMethodName());
        stopWatch.start("STEP 3: Login to BrowserStack");

        LOGGER.info("Accepting Cookie Notifications {}", testName.getMethodName());
        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.id("accept-cookie-notification")));
        driver.findElement(By.id("accept-cookie-notification")).click();

        LOGGER.info("Starting Sign In actions :: {}", testName.getMethodName());
        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Sign in")));
        driver.findElement(By.linkText("Sign in")).click();

        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email_login")));
        WebElement emailElement = driver.findElement(By.id("user_email_login"));
        WebElement passwordElement = driver.findElement(By.id("user_password"));
        WebElement submitElement = driver.findElement(By.id("user_submit"));

        emailElement.sendKeys("anirudha.khanna@gmail.com");
        passwordElement.sendKeys("Automate@123");
        submitElement.click();
        stopWatch.stop();

        LOGGER.info("STEP 4: Loading BrowserStack Live Dashboard {}", testName.getMethodName());
        stopWatch.start("STEP 4: Loading BrowserStack Live Dashboard");
        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@data-parentos='windows']")));
        waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@data-browser-version='76.0' and @data-browser='Firefox' and @data-os='win10']")));
        stopWatch.stop();

        Assert.assertEquals("Page Title has changed", PAGE_TITLE, driver.getTitle());
        LOGGER.info("Completed Live Dashboard test. {}", testName.getMethodName());
        LOGGER.info("Test :: {} Timings :: {}", testName.getMethodName(), stopWatch.prettyPrint());
    }

}
