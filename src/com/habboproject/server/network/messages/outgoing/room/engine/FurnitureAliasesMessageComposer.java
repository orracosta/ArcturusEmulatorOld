package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class FurnitureAliasesMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.FurnitureAliasesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }
}
