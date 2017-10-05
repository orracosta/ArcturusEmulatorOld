package com.habboproject.server.network.messages.incoming.room.moderation;

import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.permissions.RemoveRightsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreControllerMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RemoveRightsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int count = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        for (int i = 0; i < count; i++) {
            final int playerId = msg.readInt();

            PlayerEntity playerEntity = room.getEntities().getEntityByPlayerId(playerId);

            if (!room.getRights().hasRights(playerId)) {
                return;
            }

            room.getRights().removeRights(playerId);
            client.send(new RemoveRightsMessageComposer(playerId, room.getId()));

            if (playerEntity != null) {
                playerEntity.getPlayer().getSession().send(new YouAreControllerMessageComposer(0));
                playerEntity.removeStatus(RoomEntityStatus.CONTROLLER);
                playerEntity.addStatus(RoomEntityStatus.CONTROLLER, "0");
                playerEntity.markNeedsUpdate();
            }
        }

//        client.send(new RightsListMessageComposer(room.getId(), room.getRights().getAll()));
    }
}
