package com.habboproject.server.network.messages.incoming.user.club;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.club.ClubStatusMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ClubStatusMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new ClubStatusMessageComposer(client.getPlayer().getSubscription()));
        client.send(client.getPlayer().composeCurrenciesBalance());
    }
}
