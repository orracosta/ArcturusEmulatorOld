package com.habboproject.server.network.messages.incoming;

import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public interface Event {
    public void handle(Session client, MessageEvent msg) throws Exception;
}
