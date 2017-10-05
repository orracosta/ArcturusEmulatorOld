package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreEntry;
import com.habboproject.server.utilities.comparators.HighscoreClassicComparator;

import java.util.Collections;
import java.util.List;

/**
 * Created by brend on 03/02/2017.
 */
public class ClassicScoreboardData {
    private static final HighscoreClassicComparator comparator = new HighscoreClassicComparator();

    private int scoreType;
    private int clearType;
    private final List<HighscoreEntry> entries;

    public ClassicScoreboardData(int scoreType, int clearType, List<HighscoreEntry> entries) {
        this.scoreType = scoreType;
        this.clearType = clearType;
        this.entries = entries;
    }

    public List<HighscoreEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(List<String> users, int score) {
        List<HighscoreEntry> list = this.entries;
        synchronized (list) {
            this.entries.add(new HighscoreEntry(users, score));
            Collections.sort(this.entries, comparator);
        }
    }

    public void removeAll() {
        List<HighscoreEntry> list = this.entries;
        synchronized (list) {
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
