package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SearchFriendsMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        String query = msg.readString();

        client.send(client.getPlayer().getMessenger().search(query));
    }
}
