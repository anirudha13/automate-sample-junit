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
public class BrowserStackWebDriverConfig {

    @JsonProperty
    private String hub;

    @JsonProperty
    private String user;

    @JsonProperty
    private String key;

    @JsonProperty
    private String projectName;

    @JsonProperty
    private String buildName;

    @JsonProperty("local")
    private Boolean local;

    @JsonProperty("debug")
    private Boolean debug;

    @JsonProperty("consoleLogs")
    private Boolean consoleLogs;

    @JsonProperty("networkLogs")
    private Boolean networkLogs;

    @JsonProperty("browsers")
    private List<RemoteBrowser> remoteBrowsers;
}
