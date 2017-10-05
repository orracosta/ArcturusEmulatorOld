package com.habboproject.server.game.moderation.chatlog;

import com.habboproject.server.logging.entries.RoomChatLogEntry;

import java.util.ArrayList;
import java.util.List;


public class UserChatlogContainer {
    //private Map<Integer, List<RoomChatLogEntry>> logs;
    private List<LogSet> logs;

    public UserChatlogContainer() {
        this.logs = new ArrayList<>();
    }

    public void addAll(int roomId, List<RoomChatLogEntry> chatlogs) {
        if (chatlogs.size() < 1)
            return;

        this.logs.add(new LogSet(roomId, chatlogs));
    }

    public void dispose() {
        for (LogSet logSet : logs) {
            logSet.getLogs().clear();
        }

        this.logs.clear();
    }

    public int size() {
        return logs.size();
    }

    public List<LogSet> getLogs() {
        return this.logs;
    }

    public class LogSet {
        private int roomId;

        private List<RoomChatLogEntry> logs;

        public LogSet(int roomId, List<RoomChatLogEntry> logs) {
            this.roomId = roomId;
            this.logs = logs;
        }

        public int getRoomId() {
            return roomId;
        }

        public List<RoomChatLogEntry> getLogs() {
            return logs;
        }
    }
}
