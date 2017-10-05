package com.habboproject.server.logging.entries;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.logging.AbstractLogEntry;
import com.habboproject.server.logging.LogEntryType;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.utilities.TimeSpan;

public class RoomChatLogEntry extends AbstractLogEntry {
    private int roomId;
    private int userId;
    private String message;
    private int timestamp;

    public RoomChatLogEntry(int roomId, int userId, String message) {
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.timestamp = (int) Comet.getTime();
    }

    public RoomChatLogEntry(int roomId, int userId, String message, int timestamp) {
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void compose(IComposer msg) {
        msg.writeString(this.getTime(Comet.getTime(), this.getTimestamp()));

        msg.writeInt(this.getPlayerId());
        msg.writeString(PlayerDao.getUsernameByPlayerId(this.getPlayerId()));
        msg.writeString(this.getString());
        msg.writeBoolean(false);
    }

    private String getTime(long end, long start) {
        TimeSpan time = new TimeSpan(start * 1000, end * 1000);

        if (time.toDays() > 0) {
            return time.toDays() + " d";
        } else if (time.toHours() > 0) {
            return time.toHours() + " h";
        } else if (time.toMinutes() > 0) {
            return time.toMinutes() + " m";
        } else if (time.toSeconds() > 0) {
            return time.toSeconds() + " s";
        }

        return "0 s";
    }

    @Override
    public LogEntryType getType() {
        return LogEntryType.ROOM_CHATLOG;
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
        return this.userId;
    }
}
