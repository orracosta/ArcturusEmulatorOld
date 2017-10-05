package com.habboproject.server.network.messages.incoming.catalog.groups;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.groups.GroupElementsMessageComposer;
import com.habboproject.server.network.messages.outgoing.catalog.groups.GroupPartsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class BuyGroupDialogMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new GroupPartsMessageComposer(client.getPlayer().getRooms()));
        client.send(new GroupElementsMessageComposer());
    }
}
