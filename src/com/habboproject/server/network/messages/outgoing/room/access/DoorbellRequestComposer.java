package com.habboproject.server.network.messages.outgoing.room.access;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class DoorbellRequestComposer extends MessageComposer {
    private final String username;

    public DoorbellRequestComposer(final String username) {
        this.username = username;
    }

    @Override
    public short getId() {
        return Composers.DoorbellMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.username);
    }
}
