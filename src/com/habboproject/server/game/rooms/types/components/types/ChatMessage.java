package com.habboproject.server.game.rooms.types.components.types;

public class ChatMessage {
    private int playerId;
    private String message;

    public ChatMessage(int userId, String message) {
        this.playerId = userId;
        this.message = message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getMessage() {
        return message;
    }
}
