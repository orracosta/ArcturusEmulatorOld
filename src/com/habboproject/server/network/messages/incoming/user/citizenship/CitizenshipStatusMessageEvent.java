package com.habboproject.server.network.messages.incoming.user.citizenship;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class CitizenshipStatusMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
//        client.send(new CitizenshipStatusMessageComposer());
    }
}
