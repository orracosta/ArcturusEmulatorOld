package com.habboproject.server.utilities.comparators;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreTeamEntry;

import java.util.Comparator;

/**
 * Created by brend on 03/02/2017.
 */
public class HighscoreTeamComparator implements Comparator<HighscoreTeamEntry> {
    @Override
    public int compare(HighscoreTeamEntry o1, HighscoreTeamEntry o2) {
        return o1.getTeamScore() < o2.getTeamScore() ? 1 : -1;
    }
}
