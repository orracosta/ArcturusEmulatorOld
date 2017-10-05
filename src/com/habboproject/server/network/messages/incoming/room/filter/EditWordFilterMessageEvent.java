package com.habboproject.server.network.messages.incoming.room.filter;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class EditWordFilterMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {

    }
}
