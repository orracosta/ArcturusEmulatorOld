package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RemoveWallItemMessageComposer extends MessageComposer {
    private final int itemId;
    private final int playerId;

    public RemoveWallItemMessageComposer(final int itemId, final int playerId) {
        this.itemId = itemId;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.ItemRemoveMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.itemId);
        msg.writeInt(this.playerId);
    }
}
