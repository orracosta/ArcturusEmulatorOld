package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class LookToMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        PlayerEntity avatar = client.getPlayer().getEntity();
        if (avatar == null) {
            return;
        }

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }



        int x = msg.readInt();
        int y = msg.readInt();

        if(avatar.getMountedEntity() != null) {
            return;
        }

        avatar.lookTo(x, y);
    }
}
