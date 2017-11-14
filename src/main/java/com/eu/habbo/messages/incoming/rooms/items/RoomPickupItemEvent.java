package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

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

        if (item.getUserId() == this.client.getHabbo().getHabboInfo().getId())
        {
            room.pickUpItem(item, this.client.getHabbo());
            return;
        }
        else
        {
            if (room.hasRights(this.client.getHabbo()) || this.client.getHabbo().hasPermission("acc_modtool_ticket_q"))
            {
                if (this.client.getHabbo().hasPermission("acc_anyroomowner"))
                {
                    item.setUserId(this.client.getHabbo().getHabboInfo().getId());
                }

                room.ejectUserItem(item);
            }
        }
    }
}
