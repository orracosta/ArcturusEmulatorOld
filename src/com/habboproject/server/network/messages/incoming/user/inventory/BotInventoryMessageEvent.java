package com.habboproject.server.network.messages.incoming.user.inventory;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class BotInventoryMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new BotInventoryMessageComposer(client.getPlayer().getBots().getBots()));
    }
}
