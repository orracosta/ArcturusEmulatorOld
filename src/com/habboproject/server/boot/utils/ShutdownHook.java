package com.habboproject.server.boot.utils;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.database.queries.LogQueries;
import com.habboproject.server.storage.StorageManager;
import com.habboproject.server.storage.queries.system.StatisticsDao;
import com.habboproject.server.storage.queue.types.ItemStorageQueue;
import com.habboproject.server.storage.queue.types.PlayerDataStorageQueue;
import org.apache.log4j.Logger;


public class ShutdownHook {
    private static final Logger log = Logger.getLogger(ShutdownHook.class.getName());

    public static void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Comet is now shutting down");

                Comet.isRunning = false;

                PlayerDataStorageQueue.getInstance().shutdown();
                ItemStorageQueue.getInstance().shutdown();

                log.info("Resetting statistics");
                StatisticsDao.saveStatistics(0, 0, Comet.getBuild());

                if (LogManager.ENABLED) {
                    log.info("Updating room entry data");
                    LogQueries.updateRoomEntries();
                }

                log.info("Closing all database connections");
                StorageManager.getInstance().getConnections().shutdown();
            }
        });
    }
}
