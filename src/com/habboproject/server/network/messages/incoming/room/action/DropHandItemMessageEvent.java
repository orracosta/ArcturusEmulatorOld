package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class DropHandItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null) return;

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        client.getPlayer().getEntity().carryItem(0);
    }
}
