package com.habboproject.server.network.messages.incoming.performance;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class EventLogMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String location = msg.readString();
        String type = msg.readString();
        String action = msg.readString();
        String unk4 = msg.readString();
        int unk5 = msg.readInt();
    }
}
