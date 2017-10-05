package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.avatar.UpdateIgnoreStatusMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class IgnoreUserMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String username = msg.readString();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(username, RoomEntityType.PLAYER);

        if (playerEntity != null) {
            if (playerEntity.getPlayer().getPermissions().getRank().modTool() || playerEntity.getPlayer().getPermissions().getRank().roomIgnorable()) {
                return;
            }

            client.getPlayer().ignorePlayer(playerEntity.getPlayerId());
            client.send(new UpdateIgnoreStatusMessageComposer(1, username));
        }

    }
}
