package com.browserstack.samples;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.browserstack.driver.config.Browser;
import com.browserstack.driver.config.SampleParseConfiguration;
import com.browserstack.driver.config.WebDriverConfiguration;
import com.browserstack.test.rules.WebDriverProviderRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
//@RunWith(ParallelParameterized.class)
public class FlipkartMobileSearchTest {

    private static final Logger LOGGER = LogManager.getLogger(FlipkartMobileSearchTest.class);

    private static final String BROWSER_CONFIGURATION = "browser-configuration.yml";

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
    public final WebDriverProviderRule webDriverProvider = new WebDriverProviderRule();

    @Test
    public void testSearchTop10AndroidPhones() throws Exception {
        /* =================== Prepare ================= */
        WebDriver driver = webDriverProvider.getWebDriver(webDriverConfiguration, browser);

        /* =================== Execute & Verify ================= */
        LOGGER.debug("Running Test {} on Browser {} with WebDriver :: {}", webDriverProvider.getMethodName(),
                     browser.getName(), driver);

        WebDriverWait waitingDriver = new WebDriverWait(driver, 120);
        driver.get("https://www.flipkart.com");
        driver.findElement(By.cssSelector("input[title*='Search for products']")).sendKeys("Android Phones");
        LOGGER.debug("Test Completed");
    }
}
