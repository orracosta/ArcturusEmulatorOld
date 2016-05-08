package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCustomValues;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

/**
 * Created on 12-10-2014 11:53.
 */
public class AdvertisingSaveEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if(room == null)
            return;

        if(!room.hasRights(this.client.getHabbo()))
            return;

        HabboItem item = room.getHabboItem(this.packet.readInt());
        if(item == null)
            return;

        if(item instanceof InteractionCustomValues)
        {
            int count = this.packet.readInt();
            for(int i = 0; i < count / 2; i++)
            {
                ((InteractionCustomValues) item).values.put(this.packet.readString(), this.packet.readString());
            }

            item.setExtradata(((InteractionCustomValues) item).toExtraData());
            item.needsUpdate(true);
            Emulator.getThreading().run(item);
            room.updateItem(item);
        }
    }
}
