package com.habboproject.server.network.messages.incoming.catalog.data;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.data.CatalogOfferConfigMessageComposer;
import com.habboproject.server.network.messages.outgoing.catalog.data.GiftWrappingConfigurationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetGiftWrappingConfigurationMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new GiftWrappingConfigurationMessageComposer());
        client.send(new CatalogOfferConfigMessageComposer());
    }
}
