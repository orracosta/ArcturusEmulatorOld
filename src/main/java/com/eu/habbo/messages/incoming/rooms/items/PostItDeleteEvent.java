package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveWallItemComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

public class PostItDeleteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if(item == null || item.getUserId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        item.setRoomId(0);
        room.removeHabboItem(item);
        room.sendComposer(new RemoveWallItemComposer(item).compose());
        Emulator.getThreading().run(new QueryDeleteHabboItem(item));
    }
}
