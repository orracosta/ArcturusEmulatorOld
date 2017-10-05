package com.habboproject.server.network.messages.incoming.handshake;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class UniqueIdMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String deviceId = msg.readString();
        String fingerprint = msg.readString();

//        if(deviceId == null)
//            deviceId = fingerprint;

        client.setUniqueId(fingerprint);
    }
}
