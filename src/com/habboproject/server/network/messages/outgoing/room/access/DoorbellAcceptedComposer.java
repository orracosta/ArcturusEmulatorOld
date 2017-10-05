package com.habboproject.server.network.messages.outgoing.room.access;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class DoorbellAcceptedComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.FlatAccessibleMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString("");
    }
}
