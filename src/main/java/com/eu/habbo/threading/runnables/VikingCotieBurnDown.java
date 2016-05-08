package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionVikingCotie;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

public class VikingCotieBurnDown implements Runnable
{
    private int state;
    private final InteractionVikingCotie vikingCotie;
    private final Room room;
    private final Habbo habbo;

    public VikingCotieBurnDown(InteractionVikingCotie vikingCotie, Room room, Habbo habbo)
    {
        this.vikingCotie = vikingCotie;
        this.room = room;
        this.habbo = habbo;
        this.state = 1;
    }

    @Override
    public void run()
    {
        if(this.vikingCotie.getRoomId() > 0)
        {
            if (this.vikingCotie.getExtradata().equalsIgnoreCase("4"))
            {
                AchievementManager.progressAchievement(this.habbo, Emulator.getGameEnvironment().getAchievementManager().achievements.get("ViciousViking"));
            }
            else
            {
                this.state++;
                this.vikingCotie.setExtradata(this.state + "");
                this.room.updateItem(this.vikingCotie);

                Emulator.getThreading().run(this, 2500 - this.state * 500);
            }
        }
    }
}
