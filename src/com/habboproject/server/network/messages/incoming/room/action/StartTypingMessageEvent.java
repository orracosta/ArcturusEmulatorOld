package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.avatar.TypingStatusMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class StartTypingMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null)
            return;

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        client.getPlayer().getEntity().unIdle();
        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new TypingStatusMessageComposer(client.getPlayer().getEntity().getId(), 1));
    }
}
