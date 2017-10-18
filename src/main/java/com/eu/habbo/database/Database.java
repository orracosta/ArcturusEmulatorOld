package com.eu.habbo.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
    /**
     * Database datasource.
     */
    private HikariDataSource dataSource;

    /**
     * Database connection pool.
     */
    private DatabasePool databasePool;
    
    public Database(ConfigurationManager config)
    {

        long millis = System.currentTimeMillis();

        boolean SQLException = false;

        try
        {
            this.databasePool = new DatabasePool();
            if (!this.databasePool.getStoragePooling(config))
            {
                Emulator.getLogging().logStart("Failed to connect to the database. Shutting down...");
                SQLException = true;
                return;
            }
            this.dataSource = this.databasePool.getDatabase();
        }
        catch (Exception e)
        {
            SQLException = true;
            e.printStackTrace();
            Emulator.getLogging().logStart("Failed to connect to your database.");
            Emulator.getLogging().logStart(e.getMessage());
        }
        finally
        {
            if (SQLException)
                Emulator.prepareShutdown();
        }

        Emulator.getLogging().logStart("Database -> Connected! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    /**
     * Disposes the database and closes all connections to the database.
     */
    public void dispose()
    {
        if (this.databasePool != null)
        {
            this.databasePool.getDatabase().close();
        }

        this.dataSource.close();
    }

    public HikariDataSource getDataSource()
    {
        return this.dataSource;
    }

    public DatabasePool getDatabasePool()
    {
        return this.databasePool;
    }
}

