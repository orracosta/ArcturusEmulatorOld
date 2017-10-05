package com.habboproject.server.network.messages.outgoing.room.items.wired;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class SaveWiredMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.HideWiredConfigMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
