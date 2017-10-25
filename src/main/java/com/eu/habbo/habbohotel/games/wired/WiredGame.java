package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.wired.InteractionWiredTimer;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.HashMap;
import java.util.Map;

public class WiredGame extends Game
{
    private int timeLeft = 30;
    public WiredGame(Room room)
    {
        super(GameTeam.class, GamePlayer.class, room , false);
    }

    @Override
    public synchronized void initialise()
    {
        if(this.isRunning)
            return;

        int highestTime = 0;

        for (Map.Entry<Integer, InteractionWiredTimer> set : this.room.getRoomSpecialTypes().getWiredTimer().entrySet())
        {
            if(set.getValue().getExtradata().isEmpty())
                continue;

            if(highestTime < Integer.valueOf(set.getValue().getExtradata()))
            {
                highestTime = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        for (GameTeam t : this.teams.values())
        {
            t.initialise();
        }

        this.timeLeft = highestTime;

        if (this.timeLeft == 0)
        {
            this.timeLeft = 30;
        }

        this.start();
    }

    @Override
    public void start()
    {
        if (this.isRunning)
        {
            return;
        }

        super.start();

        WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, this.room, null);
        this.run();
    }

    /*@Override
    public void run()
    {
        if (this.timeLeft > 0)
        {
            Emulator.getThreading().run(this, 1000);
            this.timeLeft--;
            for (Map.Entry<Integer, InteractionWiredTimer> set : this.room.getRoomSpecialTypes().getWiredTimer().entrySet())
            {
                set.getValue().setExtradata(timeLeft + "");
                this.room.updateItemState(set.getValue());
            }
        }
        else
        {
            this.stop();
        }
    }*/

    @Override
    public synchronized void run()
    {
        try
        {
            if (!this.isRunning)
                return;

            if (timeLeft > 0)
            {
                Emulator.getThreading().run(this, 1000);

                this.timeLeft--;

                for (GameTeam team : this.teams.values())
                {
                    int totalScore = team.getTotalScore();

                    THashMap<Integer, InteractionFreezeScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getFreezeScoreboards(team.teamColor);

                    for (InteractionFreezeScoreboard scoreboard : scoreBoards.values())
                    {
                        if(scoreboard.getExtradata().isEmpty())
                        {
                            scoreboard.setExtradata("0");
                        }

                        int oldScore = Integer.valueOf(scoreboard.getExtradata());

                        if(oldScore == totalScore)
                            continue;

                        scoreboard.setExtradata(totalScore + "");
                        this.room.updateItemState(scoreboard);
                    }
                }

                for (Map.Entry<Integer, InteractionWiredTimer> set : this.room.getRoomSpecialTypes().getWiredTimer().entrySet())
                {
                    set.getValue().setExtradata(timeLeft + "");
                    this.room.updateItemState(set.getValue());
                }
            } else
            {
                this.stop();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        this.timeLeft = 0;

        GameTeam winningTeam = null;

        for(GameTeam team : this.teams.values())
        {
            if(winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore())
            {
                winningTeam = team;
            }
        }

        for (GameTeam team : this.teams.values())
        {
            THashSet<GamePlayer> players = new THashSet<GamePlayer>();

            players.addAll(team.getMembers());

        }

        Map<GameTeamColors, Integer> teamMemberCount = new HashMap<GameTeamColors, Integer>();
        for (Map.Entry<GameTeamColors, GameTeam> teamEntry : this.teams.entrySet())
        {
            teamMemberCount.put(teamEntry.getKey(), teamEntry.getValue().getMembers().size());
        }
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        this.removeHabbo(habbo);
        this.room.giveEffect(habbo, 0);

        this.room.giveEffect(habbo, FreezeGame.effectId + teamColor.type);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public synchronized void removeHabbo(Habbo habbo)
    {
        this.room.giveEffect(habbo, 0);
        super.removeHabbo(habbo);
    }
}