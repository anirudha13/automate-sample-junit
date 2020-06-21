package com.browserstack.test.rules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.browserstack.driver.config.Browser;
import com.browserstack.driver.config.BrowserStackWebDriverConfig;
import com.browserstack.driver.config.RealBrowser;
import com.browserstack.driver.config.WebDriverConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class WebDriverProviderRule extends TestWatcher {

    private static final Logger LOGGER = LogManager.getLogger(WebDriverProviderRule.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String SESSION_UPDATE_URL = "https://api.browserstack.com/automate/sessions/";
    private static final String STATUS_KEY = "status";
    private static final String REASON_KEY = "reason";

    private final ObjectMapper objectMapper = new ObjectMapper();
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
        if (this.webDriverConfiguration.getRunOnBrowserStack()) {
            driver = createRemoteBrowserDriver(browser);
        } else {
            driver = createLocalBrowserDriver(browser);
        }
        return driver;
    }

    private WebDriver createRemoteBrowserDriver(Browser browser) {
        RealBrowser remoteBrowser = (RealBrowser) browser;
        BrowserStackWebDriverConfig bStackConfig = webDriverConfiguration.getBrowserStackWebDriverConfig();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", remoteBrowser.getName());
        capabilities.setCapability("browserVersion", remoteBrowser.getVersion());

        Map<String, Object> browserstackOptions = new HashMap<String, Object>();
        browserstackOptions.put("os", remoteBrowser.getOs());
        browserstackOptions.put("osVersion", remoteBrowser.getOsVersion());
        browserstackOptions.put("projectName", bStackConfig.getProjectName());
        browserstackOptions.put("buildName", bStackConfig.getBuildName());
        browserstackOptions.put("sessionName", this.methodName);
        browserstackOptions.put("local", bStackConfig.getLocal());
        browserstackOptions.put("consoleLogs", bStackConfig.getConsoleLogs());
        browserstackOptions.put("networkLogs", bStackConfig.getNetworkLogs());

        capabilities.setCapability("bstack:options", browserstackOptions);

        this.driver = new RemoteWebDriver(createRemoteHubUrl(bStackConfig), capabilities);

        return driver;
    }

    private URL createRemoteHubUrl(BrowserStackWebDriverConfig bStackWebDriverConfig) {
        StringBuilder hubUrlBuilder = new StringBuilder();
        hubUrlBuilder.append("https://").append(bStackWebDriverConfig.getUser()).append(":").append(bStackWebDriverConfig.getKey())
          .append("@").append(bStackWebDriverConfig.getHub());

        try {
            return new URL(hubUrlBuilder.toString());
        } catch (MalformedURLException mue) {
            throw new Error(mue);
        }
    }

    private WebDriver createLocalBrowserDriver(Browser browser) {
        WebDriver driver = null;
        switch (browser.getName()) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", browser.getDriverPath());
                ChromeOptions chromeOptions = new ChromeOptions();
                for (Map.Entry<String, Object> entry : browser.getCapabilityMap().entrySet()) {
                    chromeOptions.setCapability(entry.getKey(), entry.getValue());
                }
                driver = new ChromeDriver(chromeOptions);
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
        LOGGER.info("Succeeded Test :: {} WebDriver Session :: {}", description.getDisplayName(), this.driver);
        if (webDriverConfiguration.getRunOnBrowserStack()) {
            Request request = createRequest("failed", "", this.driver);
            sendRequest(request);
        }
    }

    /**
     * Invoked when a test fails
     */
    protected void failed(Throwable e, Description description) {
        LOGGER.info("Failed Test :: {} WebDriver Session :: {}", description.getDisplayName(), this.driver, e);
        if (webDriverConfiguration.getRunOnBrowserStack()) {
            Request request = createRequest("failed", e.getMessage(), this.driver);
            sendRequest(request);
        }
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected void skipped(AssumptionViolatedException e, Description description) {
        LOGGER.info("Skipped Test :: {} WebDriver Session :: {}", this.methodName, this.driver);
    }

    @Override
    protected void starting(Description d) {
        methodName = d.getMethodName();
    }

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    protected void finished(Description description) {
        LOGGER.info("Skipped Test :: {} WebDriver Session :: {}", this.methodName, this.driver);
        LOGGER.info("Quitting the WebDriver instance :: {}", this.driver);
        if (this.driver != null) {
            this.driver.quit();
        }
    }


    private void sendRequest(Request request) {
        try {
            OkHttpClient okHttpClient = createAuthenticatedClient();
            Response response = okHttpClient.newCall(request).execute();
            if (response != null && response.body() != null) {
                LOGGER.debug("Got successful response :: {}", response.body().string());
            }
        } catch (IOException ioe) {
            LOGGER.error("Caught exception when sending request :: {}", request, ioe);
        }
    }

    private OkHttpClient createAuthenticatedClient() {
        String username = this.webDriverConfiguration.getBrowserStackWebDriverConfig().getUser();
        String password = this.webDriverConfiguration.getBrowserStackWebDriverConfig().getKey();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Nullable
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return okHttpClient;
    }

    private URL createRequestUrl(WebDriver driver) {
        SessionId sessionId = ((RemoteWebDriver) driver).getSessionId();
        try {
            return new URL(SESSION_UPDATE_URL + sessionId.toString() + ".json");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    protected Request createRequest(String status, String reason, WebDriver driver) {
        try {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put(STATUS_KEY, status);
            requestMap.put(REASON_KEY, reason.substring(0, 255));
            String requestBodyStr = objectMapper.writeValueAsString(requestMap);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);
            URL requestUrl = createRequestUrl(driver);
            return new Request.Builder().url(requestUrl).put(requestBody).build();
        } catch (JsonProcessingException jsonProcessingException) {
            LOGGER.error("Unable to create request :: ", jsonProcessingException);
        }
        return null;
    }
}
