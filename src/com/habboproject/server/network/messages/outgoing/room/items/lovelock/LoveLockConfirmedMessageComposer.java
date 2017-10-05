package com.habboproject.server.network.messages.outgoing.room.items.lovelock;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class LoveLockConfirmedMessageComposer extends MessageComposer {
    private final int itemId;

    public LoveLockConfirmedMessageComposer(final int itemId) {
        this.itemId = itemId;
    }

    @Override
    public short getId() {
        return Composers.LoveLockDialogueSetLockedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(itemId);
    }
}
