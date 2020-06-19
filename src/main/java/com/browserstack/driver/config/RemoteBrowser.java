package com.browserstack.driver.config;

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
public class RemoteBrowser extends AbstractBrowser {

    @JsonProperty("version")
    private String version;

    @JsonProperty("os")
    private String os;

    @JsonProperty("osVersion")
    private String osVersion;

    public RemoteBrowser() {
        super(true);
    }

}
