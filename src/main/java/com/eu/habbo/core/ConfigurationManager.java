package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.PluginManager;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager
{
    /**
     * Flag for when the ConfigurationManager has fully loaded.
     */
    public boolean loaded = false;

    /**
     * Flag for when the ConfigurationManager is still loading.
     * The configurationmanager is loaded in two parts,
     * first the config.ini is read.
     * After that the rest of the configuration is read from the database.
     */
    public boolean isLoading = false;

    /**
     * Our configurations stored in this object.
     */
    private final Properties properties;
    
    public ConfigurationManager(String path) throws Exception
    {
        this.properties = new Properties();
        
        this.reload();
    }

    /**
     * Reloads the settings from the config file.
     * @throws Exception
     */
    public void reload() throws Exception
    {
        this.isLoading = true;
        this.properties.clear();

        InputStream input = null;

        try {
            File f = new File("config.ini");
            input = new FileInputStream(f);
            this.properties.load(input);

        } catch (IOException ex) {
            Emulator.getLogging().logErrorLine("[CRITICAL] FAILED TO LOAD CONFIG.INI FILE!");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(loaded)
        {
            this.loadFromDatabase();
        }

        this.isLoading = false;
        Emulator.getLogging().logStart("Configuration Manager -> Loaded!");

        if (Emulator.getPluginManager() != null)
        {
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
        }
    }

    /**
     * Loads the settings from the database.
     */
    public void loadFromDatabase()
    {
        Emulator.getLogging().logStart("Loading configuration from database...");

        long millis = System.currentTimeMillis();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement())
        {
            if (statement.execute("SELECT * FROM emulator_settings"))
            {
                try (ResultSet set = statement.getResultSet())
                {
                    while (set.next())
                    {
                        this.properties.put(set.getString("key"), set.getString("value"));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        Emulator.getLogging().logStart("Configuration -> loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void saveToDatabase()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE emulator_settings SET `value` = ? WHERE `key` = ? LIMIT 1"))
        {
            for (Map.Entry<Object, Object> entry : this.properties.entrySet())
            {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey().toString());
                statement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Gets the string value for a specific key.
     * @param key The key to find the value for.
     * @return The string value for the key. Returns an empty string if not found.
     */
    public String getValue(String key)
    {
        return getValue(key, "");
    }

    /**
     * Gets the string value for a specific key.
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not found.
     * @return The string value for the key. Returns defaultValue when not found.
     */
    public String getValue(String key, String defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        if (!this.properties.containsKey(key)) {
            Emulator.getLogging().logErrorLine("[CONFIG] Key not found: " + key);
        }
        return this.properties.getProperty(key, defaultValue);
    }

    /**
     * Gets the boolean value for a specific key.
     * @param key The key to find the value for.
     * @return The boolean value for the key. Returns false if not found.
     */
    public boolean getBoolean(String key)
    {
        return getBoolean(key, false);
    }

    /**
     * Gets the boolean value for a specific key.
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not found.
     * @return The boolean value for the key. Returns defaultValue when not found.
     */
    public boolean getBoolean(String key, boolean defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return (getValue(key, "0").equals("1")) || (getValue(key, "false").equals("true"));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + getValue(key) + " to type boolean.");
        }
        return defaultValue;
    }

    /**
     * Gets the int value for a specific key.
     * @param key The key to find the value for.
     * @return The int value for the key. Returns 0 if not found.
     */
    public int getInt(String key)
    {
        return getInt(key, 0);
    }

    /**
     * Gets the int value for a specific key.
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not found.
     * @return The int value for the key. Returns defaultValue when not found.
     */
    public int getInt(String key, Integer defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return Integer.parseInt(getValue(key, defaultValue.toString()));
        } catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + getValue(key) + " to type integer.");
        }
        return defaultValue;
    }

    /**
     * Gets the double value for a specific key.
     * @param key The key to find the value for.
     * @return The double value for the key. Returns 0 if not found.
     */
    public double getDouble(String key)
    {
        return getDouble(key, 0.0);
    }

    /**
     * Gets the double value for a specific key.
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not found.
     * @return The double value for the key. Returns defaultValue when not found.
     */
    public double getDouble(String key, Double defaultValue)
    {
        if (this.isLoading)
            return defaultValue;

        try
        {
            return Double.parseDouble(getValue(key, defaultValue.toString()));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Failed to parse key " + key + " with value " + getValue(key) + " to type double.");
        }

        return defaultValue;
    }

    /**
     * Updates the give key.
     * @param key The key to update.
     * @param value The new value.
     */
    public void update(String key, String value)
    {
        this.properties.setProperty(key, value);
    }

    public void register(String key, String value)
    {
        if (this.properties.getProperty(key, null) != null)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO emulator_settings VALUES (?, ?)"))
        {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.update(key, value);
    }
}