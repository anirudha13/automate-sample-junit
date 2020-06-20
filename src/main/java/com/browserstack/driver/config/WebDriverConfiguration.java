package com.browserstack.driver.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
@Data
@ToString
public class WebDriverConfiguration {

    @JsonProperty("isLocalRun")
    private Boolean isLocalRun;

    @JsonProperty("local")
    private List<RealBrowser> localBrowsers;

    @JsonProperty("browserstack")
    private BrowserStackWebDriverConfig browserStackWebDriverConfig;
}
