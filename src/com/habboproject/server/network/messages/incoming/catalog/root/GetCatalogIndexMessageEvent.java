package com.habboproject.server.network.messages.incoming.catalog.root;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogIndexMessageComposer;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogPublishMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetCatalogIndexMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new CatalogIndexMessageComposer(client.getPlayer().getData().getRank()));
    }
}
