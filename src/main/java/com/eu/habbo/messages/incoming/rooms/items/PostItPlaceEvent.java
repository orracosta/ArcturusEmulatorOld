package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.items.interactions.InteractionStickyPole;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddWallItemComposer;

public class PostItPlaceEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        String location = this.packet.readString();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            if (room.hasRights(this.client.getHabbo()) || !room.getRoomSpecialTypes().getItemsOfType(InteractionStickyPole.class).isEmpty())
            {
                HabboItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemId);

                if (item != null && item instanceof InteractionPostIt)
                {
                    room.addHabboItem(item);
                    item.setExtradata("FFFF33");
                    item.setRoomId(this.client.getHabbo().getHabboInfo().getCurrentRoom().getId());
                    item.setWallPosition(location);
                    item.setUserId(this.client.getHabbo().getHabboInfo().getId());
                    item.needsUpdate(true);
                    room.sendComposer(new AddWallItemComposer(item, this.client.getHabbo().getHabboInfo().getUsername()).compose());
                    this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
                    Emulator.getThreading().run(item);
                }
            }
        }
    }
}
