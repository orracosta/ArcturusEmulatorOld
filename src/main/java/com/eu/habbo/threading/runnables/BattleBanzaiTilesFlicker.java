package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;

public class BattleBanzaiTilesFlicker implements Runnable
{
    private THashSet<HabboItem> items;
    private GameTeamColors color;
    private Room room;

    private boolean on = false;
    private int count = 0;

    public BattleBanzaiTilesFlicker(THashSet<HabboItem> items, GameTeamColors color, Room room)
    {
        this.items = items;
        this.color = color;
        this.room = room;
    }

    @Override
    public void run()
    {
        if(this.items == null || this.room == null)
            return;

        int state = 0;
        if(on)
        {
            state = (color.type * 3) + 5;
            on = false;
        }
        else
        {
            on = true;
        }

        for(HabboItem item : this.items)
        {
            item.setExtradata(state + "");
            this.room.updateItem(item);
        }

        if(this.count == 5)
        {
            for(HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
            {
                item.setExtradata("0");
                this.room.updateItem(item);
            }
            return;
        }

        this.count++;

        Emulator.getThreading().run(this, 500);
    }
}
