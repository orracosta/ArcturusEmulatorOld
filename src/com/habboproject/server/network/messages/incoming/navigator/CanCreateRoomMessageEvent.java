package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.navigator.CanCreateRoomMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class CanCreateRoomMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new CanCreateRoomMessageComposer());
    }
}
