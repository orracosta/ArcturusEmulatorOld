package com.habboproject.server.network.messages.incoming.user.inventory;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.inventory.InventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class OpenInventoryMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (!client.getPlayer().getInventory().itemsLoaded()) {
            client.getPlayer().getInventory().loadItems();
        }

        client.send(new InventoryMessageComposer(client.getPlayer().getInventory()));
    }
}
