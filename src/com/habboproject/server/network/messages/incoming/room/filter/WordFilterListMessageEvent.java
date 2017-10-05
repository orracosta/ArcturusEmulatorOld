package com.habboproject.server.network.messages.incoming.room.filter;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class WordFilterListMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
    }
}
