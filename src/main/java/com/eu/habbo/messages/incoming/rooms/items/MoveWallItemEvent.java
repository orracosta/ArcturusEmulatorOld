package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

public class MoveWallItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(!room.hasRights(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_placefurni") && !(room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) >= 2))
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
            return;
        }

        int itemId = this.packet.readInt();
        String wallPosition = this.packet.readString();

        if(itemId <= 0 || wallPosition.length() <= 13)
            return;

        HabboItem item = room.getHabboItem(itemId);

        if(item == null)
            return;

        item.setWallPosition(wallPosition);
        item.needsUpdate(true);
        room.updateItem(item);
    }
}
