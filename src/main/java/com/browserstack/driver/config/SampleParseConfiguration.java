package com.browserstack.driver.config;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class SampleParseConfiguration {

    private static final String BROWSER_CONFIGURATION = "browser-configuration.yml";

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        URL resourceURL = SampleParseConfiguration.class.getClassLoader().getResource(BROWSER_CONFIGURATION);
        WebDriverConfiguration webDriverConfiguration =  objectMapper.readValue(resourceURL, WebDriverConfiguration.class);
        System.out.println("Map read :: " + webDriverConfiguration);
    }
}
