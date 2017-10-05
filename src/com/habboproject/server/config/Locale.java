package com.habboproject.server.config;

import com.habboproject.server.storage.queries.config.LocaleDao;
import org.apache.log4j.Logger;

import java.util.Map;


public class Locale {
    /**
     * Logging for locale object
     */
    private static Logger log = Logger.getLogger(Locale.class.getName());

    /**
     * Store locale in memory
     */
    private static Map<String, String> locale;

    /**
     * Initialize the locale
     */
    public static void initialize() {
        reload();
    }

    /**
     * Load locale from the database
     */
    public static void reload() {
        if (locale != null)
            locale.clear();

        locale = LocaleDao.getAll();
        log.info("Loaded " + locale.size() + " locale strings");
    }

    /**
     * Get a locale string by the key
     *
     * @param key Retrieve from the locale by the key
     * @return String from the locale
     */
    public static String get(String key) {
        if (locale.containsKey(key))
            return locale.get(key);
        else
            return key;
    }

    public static String getOrDefault(String key, String defaultValue) {
        if (!locale.containsKey(key)) {
            return defaultValue;
        }

        return locale.get(key);
    }

    public static Map<String, String> getAll() {
        return locale;
    }
}
