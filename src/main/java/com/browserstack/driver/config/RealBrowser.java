package com.browserstack.driver.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
@Data
@ToString(of = {"name", "version", "os", "osVersion"})
public class RealBrowser implements Browser {

    @JsonProperty("version")
    private String version;

    @JsonProperty("os")
    private String os;

    @JsonProperty("osVersion")
    private String osVersion;

    @JsonProperty
    private String driverPath;

    @JsonProperty("name")
    private String name;

    @JsonProperty("capabilities")
    private Capabilities capabilities;

    public Object capability(String key) {
        return this.capabilities.capability(key);
    }

    @Override
    public Map<String, Object> getCapabilityMap() {
        return this.capabilities.getCapabilityMap();
    }

    public boolean isRemote() {
        if (driverPath != null && !driverPath.isEmpty()) {
            return false;
        }
        return true;
    }
}
