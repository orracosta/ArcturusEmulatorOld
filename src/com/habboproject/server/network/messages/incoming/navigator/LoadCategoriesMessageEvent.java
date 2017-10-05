package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.navigator.RoomCategoriesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class LoadCategoriesMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new RoomCategoriesMessageComposer(NavigatorManager.getInstance().getUserCategories(), client.getPlayer().getData().getRank()));
    }
}
