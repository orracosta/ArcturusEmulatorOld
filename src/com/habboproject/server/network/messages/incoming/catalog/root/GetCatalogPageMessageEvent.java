package com.habboproject.server.network.messages.incoming.catalog.root;

import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogPageMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetCatalogPageMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int pageId = msg.readInt();

        if (client.getPlayer().cancelPageOpen) {
            client.getPlayer().cancelPageOpen = false;
            return;
        }

        if (CatalogManager.getInstance().pageExists(pageId) && CatalogManager.getInstance().getPage(pageId).isEnabled()) {
            client.send(new CatalogPageMessageComposer(CatalogManager.getInstance().getPage(pageId)));
        }
    }
}
