package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ToggleWallItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        int itemId = this.packet.readInt();
        int state = this.packet.readInt();

        HabboItem item = room.getHabboItem(itemId);

        if (item == null)
            return;

        if(item.getBaseItem().getName().equalsIgnoreCase("poster"))
            return;

        item.needsUpdate(true);
        item.onClick(this.client, room, new Object[]{state});
        room.updateItem(item);
        Emulator.getThreading().run(item);
    }
}
