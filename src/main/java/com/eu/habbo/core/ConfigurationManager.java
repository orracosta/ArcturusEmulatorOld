package com.eu.habbo.core;

import com.eu.habbo.Emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager
{
    public boolean loaded = false;
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
        this.properties.clear();

        InputStream input = null;

        try {
            File f = new File("config.ini");
            input = new FileInputStream(f);

            this.properties.load(input);

        } catch (IOException ex) {

            ex.printStackTrace();
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

        Emulator.getLogging().logStart("Configuration Manager -> Loaded!");
    }

    /**
     * Loads the settings from the database.
     */
    public void loadFromDatabase()
    {
        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM emulator_settings");

        ResultSet set = null;
        try
        {
            set = statement.executeQuery();

            while(set.next())
            {
                this.properties.put(set.getString("key"), set.getString("value"));
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        finally
        {
            try
            {
                if(set != null)
                    set.close();

                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
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
        try
        {
            return (getValue(key, "0").equals("1")) || (getValue(key, "false").equals("true"));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
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
        try
        {
            return Integer.parseInt(getValue(key, defaultValue.toString()));
        } catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
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
}