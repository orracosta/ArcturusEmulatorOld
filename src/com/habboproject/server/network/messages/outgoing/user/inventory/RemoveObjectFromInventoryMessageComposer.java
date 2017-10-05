package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RemoveObjectFromInventoryMessageComposer extends MessageComposer {
    private final int itemId;

    public RemoveObjectFromInventoryMessageComposer(final int itemId) {
        this.itemId = itemId;
    }

    @Override
    public short getId() {
        return Composers.FurniListRemoveMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.itemId);
    }
}
