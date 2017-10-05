package com.habboproject.server.config;

import com.habboproject.server.boot.Comet;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

public class Configuration extends Properties {
    /**
     * The configuration logger
     */
    private static Logger log = Logger.getLogger(Configuration.class.getName());

    /**
     * Initialize the configuration object
     * This configuration will be loaded from the *.properties files in /config
     *
     * @param file The name of the config file
     */
    public Configuration(String file) {
        super();

        try {
            Reader stream = new InputStreamReader(new FileInputStream(file), "UTF-8");

            this.load(stream);
            stream.close();
        } catch (Exception e) {
            Comet.exit("Failed to fetch the server configuration (" + file + ")");
        }
    }

    /**
     * Override configuration
     *
     * @param config The config strings which you want to override
     */
    public void override(Map<String, String> config) {
        for (Map.Entry<String, String> configOverride : config.entrySet()) {
            if (this.containsKey(configOverride.getKey())) {
                this.remove(configOverride.getKey());
                this.put(configOverride.getKey(), configOverride.getValue());
            } else {
                this.put(configOverride.getKey(), configOverride.getValue());

            }
        }
    }

    /**
     * Get a string from the configuration
     *
     * @param key Retrieve a value from the config by the key
     * @return Value from the configuration
     */
    public String get(String key) {
        return this.getProperty(key);
    }

    public String get(String key, String fallback) {
        if (this.containsKey(fallback)) {
            return this.get(key);
        }

        return fallback;
    }

    public int getInt(String key) {
        return Integer.parseInt(this.get(key));
    }
}
