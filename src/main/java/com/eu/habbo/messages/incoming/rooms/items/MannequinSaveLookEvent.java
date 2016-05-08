package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

/**
 * Created on 12-10-2014 10:34.
 */
public class MannequinSaveLookEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null || this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId())
            return;

        HabboItem item = room.getHabboItem(this.packet.readInt());
        if(item == null)
            return;

        String[] data = item.getExtradata().split(":");
        //TODO: Only clothing not whole body part.
        if(data.length == 3)
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + this.client.getHabbo().getHabboInfo().getLook() + ":" + data[2]);
        }
        else
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + this.client.getHabbo().getHabboInfo().getLook() + ":" + this.client.getHabbo().getHabboInfo().getUsername() + "'s look.");
        }
        item.needsUpdate(true);
        Emulator.getThreading().run(item);
        room.updateItem(item);
    }
}
