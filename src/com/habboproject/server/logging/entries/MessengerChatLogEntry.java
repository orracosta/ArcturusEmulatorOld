package com.habboproject.server.logging.entries;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.logging.AbstractLogEntry;
import com.habboproject.server.logging.LogEntryType;

public class MessengerChatLogEntry extends AbstractLogEntry {
    private int senderId;
    private int receiverId;
    private String message;
    private int timestamp;

    public MessengerChatLogEntry(int senderId, int receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = (int) Comet.getTime();
    }

    @Override
    public LogEntryType getType() {
        return LogEntryType.MESSENGER_CHATLOG;
    }

    @Override
    public String getString() {
        return "To: " + this.receiverId + ", Message: " + this.message;
    }

    @Override
    public int getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getPlayerId() {
        return this.senderId;
    }
}
