package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTimer;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

public class WiredGame extends Game
{
    private int timeLeft = 30;
    public WiredGame(Room room)
    {
        super(GameTeam.class, GamePlayer.class, room , false);
    }

    @Override
    public void initialise()
    {
        for (Map.Entry<Integer, InteractionGameTimer> set : this.room.getRoomSpecialTypes().getGameTimers().entrySet())
        {
            if(set.getValue().getExtradata().isEmpty())
                continue;

            if(this.timeLeft < Integer.valueOf(set.getValue().getExtradata()))
            {
                this.timeLeft = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        if (this.timeLeft <= 30)
        {
            this.timeLeft = 30;
        }

        this.run();
    }

    @Override
    public void run()
    {
        if (this.timeLeft > 0)
        {
            Emulator.getThreading().run(this, 1000);
            this.timeLeft--;
            for (Map.Entry<Integer, InteractionGameTimer> set : this.room.getRoomSpecialTypes().getGameTimers().entrySet())
            {
                set.getValue().setExtradata(timeLeft + "");
                this.room.updateItemState(set.getValue());
            }
        }
        else
        {
            this.stop();
        }
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        this.room.giveEffect(habbo, FreezeGame.effectId + teamColor.type);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public synchronized void removeHabbo(Habbo habbo)
    {
        this.room.giveEffect(habbo, 0);
    }
}