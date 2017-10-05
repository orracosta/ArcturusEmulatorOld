package com.habboproject.server.network.messages.incoming.room.moderation;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.settings.RoomPlayerUnbannedMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RoomUnbanUserMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int playerId = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        if (room.getRights().hasBan(playerId)) {
            room.getRights().removeBan(playerId);
        }

        client.send(new RoomPlayerUnbannedMessageComposer(room.getId(), playerId));
    }
}
