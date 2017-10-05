package com.habboproject.server.logging;

import com.habboproject.server.logging.containers.LogEntryContainer;
import com.habboproject.server.logging.containers.RoomVisitContainer;

import java.util.concurrent.TimeUnit;


public class LogStore {
    private static final TimeUnit QUEUE_FLUSH_UNIT = TimeUnit.MINUTES;
    private static final int QUEUE_FLUSH_TIME = 1;

    // Containers
    private RoomVisitContainer roomVisitContainer;
    private LogEntryContainer logEntryContainer;

    public LogStore() {
        if (!LogManager.ENABLED)
            return;

        // Register the containers
        roomVisitContainer = new RoomVisitContainer();
        logEntryContainer = new LogEntryContainer();
    }

    public RoomVisitContainer getRoomVisitContainer() {
        return roomVisitContainer;
    }

    public LogEntryContainer getLogEntryContainer() {
        return logEntryContainer;
    }
}
