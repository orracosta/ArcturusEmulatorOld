package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.HeightmapMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class LoadHeightmapMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom().getModel() == null) {
            return;
        }

        client.sendQueue(new HeightmapMessageComposer(client.getPlayer().getEntity().getRoom()));
        client.sendQueue(client.getPlayer().getEntity().getRoom().getModel().getRelativeHeightmapMessage());
        client.flush();
    }
}
