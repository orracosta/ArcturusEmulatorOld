package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class HandItemMessageComposer extends MessageComposer {
    private final int avatarId;
    private final int handItemId;

    public HandItemMessageComposer(final int avatarId, final int handItemId) {
        this.avatarId = avatarId;
        this.handItemId = handItemId;
    }

    @Override
    public short getId() {
        return Composers.CarryObjectMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(avatarId);
        msg.writeInt(handItemId);
    }
}
