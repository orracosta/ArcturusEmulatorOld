package com.habboproject.server.storage;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.utilities.Initializable;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;


public class StorageManager implements Initializable {
    private static StorageManager storageManagerInstance;
    private static Logger log = Logger.getLogger(StorageManager.class.getName());
    private HikariDataSource connections = null;

    public StorageManager() {

    }

    @Override
    public void initialize() {
        boolean isConnectionFailed = false;

        try {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(
                    "jdbc:mysql://" + Comet.getServer().getConfig().get("comet.db.host") + ":" + Comet.getServer().getConfig().get("comet.db.port") +
                            "/" + Comet.getServer().getConfig().get("comet.db.name") + "?tcpKeepAlive=" + Comet.getServer().getConfig().get("comet.db.pool.tcpKeepAlive") +
                            "&autoReconnect=" + Comet.getServer().getConfig().get("comet.db.pool.autoReconnect")
            );

            config.setUsername(Comet.getServer().getConfig().get("comet.db.username"));
            config.setPassword(Comet.getServer().getConfig().get("comet.db.password"));

            config.setMaximumPoolSize(Integer.parseInt(Comet.getServer().getConfig().get("comet.db.pool.max")));

            log.info("Connecting to the MySQL server");

            this.connections = new HikariDataSource(config);
        } catch (Exception e) {
            isConnectionFailed = true;
            log.error("Failed to connect to MySQL server", e);
            System.exit(0);
        } finally {
            if (!isConnectionFailed) {
                log.info("Connection to MySQL server was successful");
            }
        }

        SqlHelper.init(this);
    }

    public static StorageManager getInstance() {
        if (storageManagerInstance == null)
            storageManagerInstance = new StorageManager();

        return storageManagerInstance;
    }

    public HikariDataSource getConnections() {
        return this.connections;
    }
}
