package com.habboproject.server.game.rooms.filter;

public class FilterResult {
    private boolean isBlocked;
    private boolean wasModified;
    private String message;

    public FilterResult(String chatMessage) {
        this.isBlocked = false;
        this.wasModified = false;
        this.message = chatMessage;
    }

    public FilterResult(boolean isBlocked, String chatMessage) {
        this.isBlocked = isBlocked;
        this.wasModified = false;
        this.message = chatMessage;
    }

    public FilterResult(String chatMessage, boolean wasModified) {
        this.isBlocked = false;
        this.wasModified = wasModified;
        this.message = chatMessage;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public String getMessage() {
        return message;
    }

    public boolean wasModified() {
        return wasModified;
    }
}
