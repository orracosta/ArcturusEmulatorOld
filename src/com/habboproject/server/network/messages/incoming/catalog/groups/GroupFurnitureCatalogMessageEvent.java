package com.habboproject.server.network.messages.incoming.catalog.groups;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GroupFurnitureCatalogMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new GroupDataMessageComposer(client.getPlayer().getGroups(), client.getPlayer().getId()));
    }
}
