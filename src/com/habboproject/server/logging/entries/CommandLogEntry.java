package com.habboproject.server.logging.entries;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.logging.AbstractLogEntry;
import com.habboproject.server.logging.LogEntryType;

public class CommandLogEntry extends AbstractLogEntry {
    private int roomId;
    private int playerId;
    private String message;
    private int timestamp;

    public CommandLogEntry(int roomId, int playerId, String message) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.message = message;
        this.timestamp = (int) Comet.getTime();
    }

    @Override
    public LogEntryType getType() {
        return LogEntryType.COMMAND;
    }

    @Override
    public String getString() {
        return this.message;
    }

    @Override
    public int getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getRoomId() {
        return this.roomId;
    }

    @Override
    public int getPlayerId() {
        return this.playerId;
    }
}


