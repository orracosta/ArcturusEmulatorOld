package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreTeamEntry;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.types.HighscorePlayer;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by brend on 03/02/2017.
 */
public class TeamScoreboardData {
    private int scoreType;
    private int clearType;

    private final Map<Integer, List<HighscoreTeamEntry>> entries;

    public TeamScoreboardData(int scoreType, int clearType, Map<Integer, List<HighscoreTeamEntry>> entries) {
        this.scoreType = scoreType;
        this.clearType = clearType;
        this.entries = entries;
    }

    public Map<Integer, List<HighscoreTeamEntry>> getEntries() {
        return this.entries;
    }

    public void addEntry(List<HighscorePlayer> users, int teamScore) {
        Map<Integer, List<HighscoreTeamEntry>> map = this.entries;

        synchronized (map) {
            int index = this.entries.size();
            this.entries.put(index, Lists.newArrayList());
            this.entries.get(index).add(new HighscoreTeamEntry(users, teamScore));
        }
    }

    public void updateEntry(int index, List<HighscorePlayer> users, int teamScore) {
        Map<Integer, List<HighscoreTeamEntry>> map = this.entries;

        synchronized (map) {
            for (HighscoreTeamEntry entry : this.entries.get(index)) {
                entry.setTeamScore(entry.getTeamScore() + teamScore);
            }
        }
    }

    public void removeAll() {
        Map<Integer, List<HighscoreTeamEntry>> map = this.entries;
        synchronized (map) {
            this.entries.clear();
        }
    }

    public int getScoreType() {
        return this.scoreType;
    }

    public void setScoreType(int scoreType) {
        this.scoreType = scoreType;
    }

    public int getClearType() {
        return this.clearType;
    }

    public void setClearType(int clearType) {
        this.clearType = clearType;
    }

}
