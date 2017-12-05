package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class MannequinSaveNameEvent extends MessageHandler
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
        String name = this.packet.readString();
        if(data.length == 3)
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + data[1] + ":" + name);
        }
        else
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + this.client.getHabbo().getHabboInfo().getLook() + ":" + name);
        }
        item.needsUpdate(true);
        Emulator.getThreading().run(item);
        room.updateItem(item);
    }
}
