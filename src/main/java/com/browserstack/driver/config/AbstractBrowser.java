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
@ToString
public abstract class AbstractBrowser implements Browser {

    @JsonProperty("name")
    private String name;

    @JsonProperty("capabilities")
    private Capabilities capabilities;

    private Boolean isRemote = false;

    public AbstractBrowser(Boolean isRemote) {
        this.isRemote = isRemote;
    }

    public boolean isRemote() {
        return this.isRemote;
    }

    public Object capability(String key) {
        return this.capabilities.capability(key);
    }

    @Override
    public Map<String, Object> getCapabilityMap() {
        return this.capabilities.getCapabilityMap();
    }
}
