package com.habboproject.server.game.players.components.types.messenger;

public class MessengerFriendData {
    private String username;
    private String figure;
    private String motto;

    public MessengerFriendData(String username, String figure, String motto) {
        this.username = username;
        this.figure = figure;
        this.motto = motto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }
}
