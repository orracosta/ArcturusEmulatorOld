package com.habboproject.server.utilities.comparators;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreEntry;

import java.util.Comparator;

/**
 * Created by brend on 03/02/2017.
 */
public class HighscoreClassicComparator implements Comparator<HighscoreEntry> {
    @Override
    public int compare(HighscoreEntry o1, HighscoreEntry o2) {
        return o1.getScore() < o2.getScore() ? 1 : -1;
    }
}
