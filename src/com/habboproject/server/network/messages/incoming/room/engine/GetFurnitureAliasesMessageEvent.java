package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.FurnitureAliasesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class GetFurnitureAliasesMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new FurnitureAliasesMessageComposer());
    }
}
