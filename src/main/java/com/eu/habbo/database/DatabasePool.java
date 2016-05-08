package com.eu.habbo.database;

import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class DatabasePool
{
    private HikariDataSource database;

    public boolean getStoragePooling(ConfigurationManager config)
    {
        try
        {
            HikariConfig databaseConfiguration = new HikariConfig();
            databaseConfiguration.setMaximumPoolSize(20);
            databaseConfiguration.setInitializationFailFast(true);
            databaseConfiguration.setJdbcUrl("jdbc:mysql://" + config.getValue("db.hostname", "localhost") + ":" + config.getValue("db.port", "3306")+ "/" + config.getValue("db.database", "habbo"));
            databaseConfiguration.addDataSourceProperty("serverName", config.getValue("db.hostname", "localhost"));
            databaseConfiguration.addDataSourceProperty("port", config.getValue("db.port", "3306"));
            databaseConfiguration.addDataSourceProperty("databaseName", config.getValue("db.database", "habbo"));
            databaseConfiguration.addDataSourceProperty("user", config.getValue("db.username"));
            databaseConfiguration.addDataSourceProperty("password", config.getValue("db.password"));
            databaseConfiguration.setAutoCommit(true);
            databaseConfiguration.setConnectionTimeout(3400L);
            databaseConfiguration.setLeakDetectionThreshold(90000L);
            databaseConfiguration.setMaxLifetime(2874000L);
            databaseConfiguration.setIdleTimeout(2874000L);
            databaseConfiguration.setDriverClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            this.database = new HikariDataSource(databaseConfiguration);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public HikariDataSource getDatabase()
    {
        return this.database;
    }
}