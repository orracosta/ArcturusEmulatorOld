package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.types;

/**
 * Created by brend on 03/02/2017.
 */
public class HighscorePlayer {
    private final String username;
    private int playerScore;

    public HighscorePlayer(String username, int playerScore) {
        this.username = username;
        this.playerScore = playerScore;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPlayerScore() {
        return this.playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
