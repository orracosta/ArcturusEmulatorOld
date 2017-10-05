package com.habboproject.server.network.messages.incoming.landing;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.landing.HotelViewItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class LandingLoadWidgetMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final String text = msg.readString();
        final String[] splitText = text.split(",");

        if (text.isEmpty() || splitText.length < 2) {
            client.send(new HotelViewItemMessageComposer("", ""));
            return;
        }

        if (splitText[1].equals("gamesmaker")) return;

        client.send(new HotelViewItemMessageComposer(text, splitText[1]));
    }
}
