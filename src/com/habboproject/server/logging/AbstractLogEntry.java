package com.habboproject.server.logging;

public abstract class AbstractLogEntry {
    public abstract LogEntryType getType();

    public abstract String getString();

    public abstract int getTimestamp();

    public int getRoomId() {
        return -1;
    }

    public int getPlayerId() {
        return -1;
    }
}
