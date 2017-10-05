package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class UseWallItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        int itemId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        RoomItemWall item = client.getPlayer().getEntity().getRoom().getItems().getWallItem(itemId);

        if (item == null) {
            return;
        }

        int requestData = msg.readInt();

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        item.onInteract(client.getPlayer().getEntity(), requestData, false);
    }
}
