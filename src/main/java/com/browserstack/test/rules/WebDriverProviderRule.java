package com.browserstack.test.rules;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.browserstack.driver.config.Browser;
import com.browserstack.driver.config.LocalBrowser;
import com.browserstack.driver.config.WebDriverConfiguration;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class WebDriverProviderRule extends TestWatcher {

    private static final Logger LOGGER = LogManager.getLogger(WebDriverProviderRule.class);

    private String methodName;
    private WebDriverConfiguration webDriverConfiguration;
    private WebDriver driver;

    /**
     * @return the name of the currently-running test method
     */
    public String getMethodName() {
        return methodName;
    }

    public WebDriver getWebDriver(WebDriverConfiguration webDriverConfiguration, Browser browser) {
        this.webDriverConfiguration = webDriverConfiguration;
        if (this.webDriverConfiguration.getIsLocalRun()) {
            driver = createLocalBrowserDriver(browser);
        } else {
            driver = createRemoteBrowserDriver();
        }
        return driver;
    }

    private WebDriver createRemoteBrowserDriver() {
        return null;
    }

    private WebDriver createLocalBrowserDriver(Browser browser) {
        if ( !(browser instanceof LocalBrowser) && !browser.isRemote()) {
            throw new Error("Incorrect configuration for Browser :: " + browser);
        }
        WebDriver driver = null;
        switch (browser.getName()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", ((LocalBrowser) browser).getDriverPath());
                driver = new ChromeDriver();
                break;
            case "safari":
                SafariOptions safariOptions = new SafariOptions();
                for (Map.Entry<String, Object> entry : browser.getCapabilityMap().entrySet()) {
                    safariOptions.setCapability(entry.getKey(), entry.getValue());
                }
                driver = new SafariDriver(safariOptions);
                break;
            default:
                break;
        }
        return driver;
    }

    /**
     * Invoked when a test succeeds
     */
    protected void succeeded(Description description) {
        LOGGER.info("Succeeded Test :: {} WebDriver Session :: {}", this.methodName,  this.driver);
    }

    /**
     * Invoked when a test fails
     */
    protected void failed(Throwable e, Description description) {
        LOGGER.info("Failed Test :: {} WebDriver Session :: {}", this.methodName,  this.driver);
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected void skipped(AssumptionViolatedException e, Description description) {
        LOGGER.info("Skipped Test :: {} WebDriver Session :: {}", this.methodName,  this.driver);
    }

    @Override
    protected void starting(Description d) {
        methodName = d.getMethodName();
    }

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    protected void finished(Description description) {
        LOGGER.info("Skipped Test :: {} WebDriver Session :: {}", this.methodName,  this.driver);
    }

}
