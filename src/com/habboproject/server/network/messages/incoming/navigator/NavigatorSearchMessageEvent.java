package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.game.navigator.types.search.NavigatorSearchService;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class NavigatorSearchMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String category = msg.readString();
        String data = msg.readString();

        NavigatorSearchService.getInstance().submitRequest(client.getPlayer(), category, data);
    }
}
