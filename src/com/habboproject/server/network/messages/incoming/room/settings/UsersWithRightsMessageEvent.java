package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.settings.RightsListMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class UsersWithRightsMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int roomId = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }

        client.send(new RightsListMessageComposer(room.getId(), room.getRights().getAll()));
    }
}
