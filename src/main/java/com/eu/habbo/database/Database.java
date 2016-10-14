package com.eu.habbo.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
    private HikariDataSource dataSource;
    private DatabasePool databasePool;
    private ConfigurationManager config;
    
    public Database(ConfigurationManager config)
    {
        this.config = config;

        long millis = System.currentTimeMillis();

        boolean SQLException = false;

        try
        {
            this.databasePool = new DatabasePool();
            if (!this.databasePool.getStoragePooling(config))
            {
                Emulator.getLogging().logErrorLine("Failed to connect to the database. Shutting down...");
                SQLException = true;
                return;
            }
            this.dataSource = this.databasePool.getDatabase();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
            SQLException = true;
        }
        finally
        {
            if(SQLException)
            {
                Emulator.prepareShutdown();
            }
        }

        Emulator.getLogging().logStart("Database -> Connected! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    
    public PreparedStatement prepare(String query)
    {
        PreparedStatement statement = null;

        try
        {
            statement = this.dataSource.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        if(statement == null)
        {
            dispose();
            this.databasePool.getStoragePooling(config);
            this.dataSource = this.databasePool.getDatabase();

            return this.prepare(query);
        }

        return statement;
    }

    public void dispose()
    {
        if (this.databasePool != null)
        {
            this.databasePool.getDatabase().close();
        }
    }
}

