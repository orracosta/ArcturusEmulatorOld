package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 10-3-2015 10:10.
 */
public class TriggerOneWayGateEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        int itemId = this.packet.readInt();
        HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

        if(item == null)
            return;

        if(item instanceof InteractionOneWayGate)
        {
            if(!item.getExtradata().equals("0") || this.client.getHabbo().getRoomUnit().isTeleporting)
                return;

            item.onClick(this.client, this.client.getHabbo().getHabboInfo().getCurrentRoom(), null);
        }

    }
}
