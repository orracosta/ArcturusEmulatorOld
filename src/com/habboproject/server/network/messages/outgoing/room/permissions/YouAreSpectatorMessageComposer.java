package com.habboproject.server.network.messages.outgoing.room.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class YouAreSpectatorMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.YouAreSpectatorMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
