package com.habboproject.server.network.messages.incoming.performance;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.misc.PingMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RequestLatencyTestMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new PingMessageComposer());
    }
}
