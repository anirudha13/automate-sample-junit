package com.browserstack.driver.config;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public interface Browser {

    boolean isRemote();

    Capabilities getCapabilities();

    Map<String, Object> getCapabilityMap();

    Object capability(String key);

    String getName();

}
