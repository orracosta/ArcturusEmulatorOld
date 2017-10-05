package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.navigator.NavigatorMetaDataMessageComposer;
import com.habboproject.server.network.messages.outgoing.navigator.NavigatorCollapsedCategoriesMessageComposer;
import com.habboproject.server.network.messages.outgoing.navigator.NavigatorLiftedRoomsMessageComposer;
import com.habboproject.server.network.messages.outgoing.navigator.NavigatorPreferencesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class InitializeNavigatorMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.sendQueue(new NavigatorPreferencesMessageComposer())
                .sendQueue(new NavigatorMetaDataMessageComposer())
                .sendQueue(new NavigatorLiftedRoomsMessageComposer())
                .sendQueue(new NavigatorCollapsedCategoriesMessageComposer());

        client.flush();
    }
}
