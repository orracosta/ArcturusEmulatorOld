package com.habboproject.server.network.messages.incoming.landing;

import com.habboproject.server.game.landing.LandingManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.landing.PromoArticlesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RefreshPromoArticlesMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new PromoArticlesMessageComposer(LandingManager.getInstance().getArticles()));
    }
}
