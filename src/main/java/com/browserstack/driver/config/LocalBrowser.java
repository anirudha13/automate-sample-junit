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
public class LocalBrowser extends AbstractBrowser {

    @JsonProperty
    private String driverPath;

    public LocalBrowser() {
        super(false);
    }
}
