package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 21-9-2014 12:21.
 */
public class RoomPickupItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int unknown = this.packet.readInt();
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if (item == null)
            return;

        if(item instanceof InteractionPostIt)
            return;

        if(item.getUserId() == this.client.getHabbo().getHabboInfo().getId() || (room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) >= 2) || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            if (room.getGuildId() == 0 && this.client.getHabbo().hasPermission("acc_anyroomowner"))
                item.setUserId(this.client.getHabbo().getHabboInfo().getId());

            room.pickUpItem(item, this.client.getHabbo());
        }
    }
}
