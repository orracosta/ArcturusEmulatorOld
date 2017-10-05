package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ExchangeItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        int itemId = msg.readInt();

        if (client.getPlayer().getEntity() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }


        RoomItemFloor item = room.getItems().getFloorItem(itemId);

        if (item == null) {
            return;
        }

        if (item.getOwner() != client.getPlayer().getId()) {
            return;
        }

        int value;
        boolean isDiamond = false;

        if(!item.getDefinition().getItemName().startsWith("CF_") && !item.getDefinition().getItemName().startsWith("CFC_")) {
            return;
        }

        if (item.getDefinition().getItemName().contains("_diamond_")) {
            isDiamond = true;
            value = Integer.parseInt(item.getDefinition().getItemName().split("_diamond_")[1]);
        } else {
            value = Integer.parseInt(item.getDefinition().getItemName().split("_")[1]);
        }

        room.getItems().removeItem(item, client, false, true);

        if (isDiamond) {
            client.getPlayer().getData().increasePoints(value);
        } else {
            client.getPlayer().getData().increaseCredits(value);
        }

        client.getPlayer().sendBalance();
        client.getPlayer().getData().save();
    }
}
