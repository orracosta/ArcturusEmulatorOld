package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class OpenDiceMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) {
        int itemId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(itemId);

        if (item == null) {
            return;
        }

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        item.onInteract(client.getPlayer().getEntity(), -1, false);
    }
}
