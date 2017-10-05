package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.settings.MuteAllInRoomMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class MuteRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null)
            return;

        Room room = client.getPlayer().getEntity().getRoom();

        if (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if (room.hasRoomMute()) {
            for(RoomEntity entity : room.getEntities().getAllEntities().values()) {
                entity.setRoomMuted(false);
            }

            room.setRoomMute(false);
        } else {
            room.setRoomMute(true);

            for(RoomEntity entity : room.getEntities().getAllEntities().values()) {
                entity.setRoomMuted(true);
            }
        }

        room.getEntities().broadcastMessage(new MuteAllInRoomMessageComposer(room.hasRoomMute()));
    }
}
