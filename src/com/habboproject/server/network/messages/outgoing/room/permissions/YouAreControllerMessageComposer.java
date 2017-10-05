package com.habboproject.server.network.messages.outgoing.room.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class YouAreControllerMessageComposer extends MessageComposer {
    private final int rightId;

    public YouAreControllerMessageComposer(int rightId) {
        this.rightId = rightId;
    }

    @Override
    public short getId() {
        return Composers.YouAreControllerMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(rightId);
    }
}
