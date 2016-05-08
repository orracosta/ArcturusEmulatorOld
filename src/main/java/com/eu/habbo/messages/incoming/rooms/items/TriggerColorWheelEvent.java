package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 2-11-2014 21:10.
 */
public class TriggerColorWheelEvent extends MessageHandler
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
            item.onClick(this.client, room, null);
        }
    }
}
