package com.habboproject.server.network.messages.incoming.catalog.data;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.data.CatalogOfferConfigMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class CatalogOfferConfigMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new CatalogOfferConfigMessageComposer());
    }
}
