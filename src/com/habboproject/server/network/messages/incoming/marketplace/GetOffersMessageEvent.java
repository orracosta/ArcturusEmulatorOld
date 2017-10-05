package com.habboproject.server.network.messages.incoming.marketplace;

import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.marketplace.OffersMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 31/01/2017.
 */
public class GetOffersMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int minPrice = msg.readInt();
        int maxPrice = msg.readInt();

        String query = msg.readString();

        int sort = msg.readInt();

        client.send(new OffersMessageComposer(MarketplaceManager.getInstance().getOffers(minPrice, maxPrice, query, sort)));
    }
}
