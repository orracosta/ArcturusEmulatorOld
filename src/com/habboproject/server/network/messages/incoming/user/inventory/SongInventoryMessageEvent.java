package com.habboproject.server.network.messages.incoming.user.inventory;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.inventory.SongInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class SongInventoryMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (!client.getPlayer().getInventory().itemsLoaded()) {
            client.getPlayer().getInventory().loadItems();
        }

        client.send(new SongInventoryMessageComposer(client.getPlayer().getInventory().getSongs()));
    }
}
