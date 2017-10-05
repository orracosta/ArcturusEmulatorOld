package com.habboproject.server.network.messages.incoming.marketplace;

import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.marketplace.OwnOffersMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 31/01/2017.
 */
public class GetOwnOffersMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new OwnOffersMessageComposer(MarketplaceManager.getInstance().getOwnOffers(client.getPlayer().getId())));
    }
}
