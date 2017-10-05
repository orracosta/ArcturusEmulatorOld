package com.habboproject.server.network.messages.incoming.room.moderation;

import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreControllerMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class RemoveOwnRightsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null) {
            return;
        }

        if (client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId())) {
            client.getPlayer().getEntity().getRoom().getRights().removeRights(client.getPlayer().getId());

            client.send(new YouAreControllerMessageComposer(0));

            client.getPlayer().getEntity().removeStatus(RoomEntityStatus.CONTROLLER);
            client.getPlayer().getEntity().addStatus(RoomEntityStatus.CONTROLLER, "0");
            client.getPlayer().getEntity().markNeedsUpdate();
        }
    }
}
