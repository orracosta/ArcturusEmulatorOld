package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;

public class CloseGate implements Runnable
{
    private final HabboItem guildGate;
    private final Room room;

    public CloseGate(HabboItem guildGate, Room room)
    {
        this.guildGate = guildGate;
        this.room = room;
    }

    @Override
    public void run()
    {
        if(this.guildGate.getRoomId() == this.room.getId())
        {
            if(this.room.isLoaded())
            {
                if(this.room.getHabbosAt(this.guildGate.getX(), this.guildGate.getY()).isEmpty())
                {
                    this.guildGate.setExtradata("0");
                    this.room.updateItem(this.guildGate);
                    this.guildGate.needsUpdate(true);
                }
            }
        }
    }
}
