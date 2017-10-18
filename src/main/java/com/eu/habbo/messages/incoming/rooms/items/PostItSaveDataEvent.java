package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class PostItSaveDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        String color = this.packet.readString();
        String text = this.packet.readString();

        text = text.replace(((char) 9) + "", "");
        if(text.startsWith("#") || text.startsWith(" #"))
        {
            String colorCheck = text.split(" ")[0].replace(" ", "").replace(" #", "").replace("#", "");

            if(colorCheck.length() == 6)
            {
                color = colorCheck;
                text = text.replace("#" + colorCheck + " ", "");
            }
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if(item == null || !(item instanceof InteractionPostIt))
            return;

        if(!color.equalsIgnoreCase("FFFF33") && !room.hasRights(this.client.getHabbo())&& item.getUserId() != this.client.getHabbo().getHabboInfo().getId())
        {
            if(!text.startsWith(item.getExtradata().replace(item.getExtradata().split(" ")[0], "")))
            {
                return;
            }
        }
        else
        {
            if(!room.hasRights(this.client.getHabbo()) && item.getUserId() != this.client.getHabbo().getHabboInfo().getId())
                return;
        }

        if(color.isEmpty())
            color = "FFFF33";

        item.setExtradata(color + " " + text);
        item.needsUpdate(true);
        room.updateItem(item);
        Emulator.getThreading().run(item);
    }
}
