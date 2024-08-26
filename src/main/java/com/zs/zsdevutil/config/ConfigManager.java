package com.zs.zsdevutil.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class ConfigManager {
    private static final Config config;

    static {
        config = buildConfig();
    }

    private ConfigManager() {
        // Private constructor to prevent instantiation
    }

    private static Config buildConfig() {
        return ConfigFactory.systemEnvironment()  // Highest priority (4th level)
            .withFallback(ConfigFactory.systemProperties())  // 3rd level
            .withFallback(loadFileConfig("application"))  // 2nd level
            .withFallback(loadResourceConfig("application"))  // 1st level (lowest priority)
            .resolve();  // Resolve substitutions
    }

    private static Config loadFileConfig(String baseName) {
        File yamlFile = new File(baseName + ".yaml");
        File ymlFile = new File(baseName + ".yml");
        File propertiesFile = new File(baseName + ".properties");

        if (yamlFile.exists()) {
            return ConfigFactory.parseFile(yamlFile);
        } else if (ymlFile.exists()) {
            return ConfigFactory.parseFile(ymlFile);
        } else if (propertiesFile.exists()) {
            return ConfigFactory.parseFile(propertiesFile);
        }
        return ConfigFactory.empty();
    }

    private static Config loadResourceConfig(String baseName) {
        Config yamlConfig = ConfigFactory.parseResources(baseName + ".yaml");
        Config ymlConfig = ConfigFactory.parseResources(baseName + ".yml");
        Config propertiesConfig = ConfigFactory.parseResources(baseName + ".properties");

        if (!yamlConfig.isEmpty()) {
            return yamlConfig;
        } else if (!ymlConfig.isEmpty()) {
            return ymlConfig;
        } else if (!propertiesConfig.isEmpty()) {
            return propertiesConfig;
        }
        return ConfigFactory.empty();
    }

    public static String getString(String key) {
        return config.getString(key);
    }

    // Add more static getter methods as needed
}