package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

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

        String look = "";

        for (String s : this.client.getHabbo().getHabboInfo().getLook().split("\\."))
        {
            if (!s.contains("hr") && !s.contains("hd") && !s.contains("he") && !s.contains("ea") && !s.contains("ha") && !s.contains("fa"))
            {
                look += s + ".";
            }
        }

        if (!look.isEmpty())
        {
            look = look.substring(0, look.length() - 1);
        }

        if(data.length == 3)
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toLowerCase() + ":" + look + ":" + data[2]);
        }
        else
        {
            item.setExtradata(this.client.getHabbo().getHabboInfo().getGender().name().toLowerCase() + ":" + look + ":" + this.client.getHabbo().getHabboInfo().getUsername() + "'s look.");
        }

        item.needsUpdate(true);
        Emulator.getThreading().run(item);
        room.updateItem(item);
    }
}
