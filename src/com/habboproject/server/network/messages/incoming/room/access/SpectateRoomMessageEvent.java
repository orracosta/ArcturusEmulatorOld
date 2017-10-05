package com.habboproject.server.network.messages.incoming.room.access;

import com.habboproject.server.game.rooms.RoomQueue;
import com.habboproject.server.game.rooms.RoomSpectator;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class SpectateRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        int roomId = client.getPlayer().getEntity().getRoom().getId();
        if (!RoomQueue.getInstance().hasQueue(roomId)) {
            client.send(new HotelViewMessageComposer());
            return;
        }

        client.getPlayer().setSpectatorRoomId(roomId);

        RoomQueue.getInstance().removePlayerFromQueue(roomId, client.getPlayer().getId());

        if (!RoomSpectator.getInstance().hasSpectator(roomId)) {
            RoomSpectator.getInstance().addSpectatorsInRoom(roomId);
        }

        RoomSpectator.getInstance().addPlayerToSpectateMode(roomId, client.getPlayer().getId());

        client.send(new RoomForwardMessageComposer(roomId));
    }
}

