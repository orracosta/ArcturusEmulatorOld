package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCustomValues;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoomAds;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

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
                String key = this.packet.readString();
                String value = this.packet.readString();

                if (!Emulator.getConfig().getBoolean("camera.use.https"))
                {
                    value = value.replace("https://", "http://");
                }

                ((InteractionCustomValues) item).values.put(key, value);
            }

            item.setExtradata(((InteractionCustomValues) item).toExtraData());
            item.needsUpdate(true);
            Emulator.getThreading().run(item);
            room.updateItem(item);
        }
    }
}
