package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.util.pathfinding.PathFinder;

public class CloseDiceEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if(item != null)
        {
            if(PathFinder.tilesAdjecent(item.getX(), item.getY(), this.client.getHabbo().getRoomUnit().getX(), this.client.getHabbo().getRoomUnit().getY()))
            {
                if(!item.getExtradata().equals("-1"))
                {
                    item.setExtradata("0");
                    item.needsUpdate(true);
                    Emulator.getThreading().run(item);
                    room.updateItem(item);
                }
            }
        }
    }
}
