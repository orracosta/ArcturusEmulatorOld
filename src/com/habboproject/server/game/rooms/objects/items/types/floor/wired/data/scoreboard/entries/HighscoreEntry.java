package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries;

import java.util.List;

/**
 * Created by brend on 03/02/2017.
 */
public class HighscoreEntry {
    private List<String> users;
    private int score;

    public HighscoreEntry(List<String> users, int score) {
        this.users = users;
        this.score = score;
    }

    public List<String> getUsers() {
        return this.users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
