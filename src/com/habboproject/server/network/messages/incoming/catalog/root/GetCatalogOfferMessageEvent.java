package com.habboproject.server.network.messages.incoming.catalog.root;

import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogOfferMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetCatalogOfferMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int offerId = msg.readInt();

        if (offerId == -1)
            return;

        CatalogItem catalogItem = CatalogManager.getInstance().getCatalogItemByOfferId(offerId);

        if (catalogItem != null) {
            client.send(new CatalogOfferMessageComposer(catalogItem));
        }
    }
}
