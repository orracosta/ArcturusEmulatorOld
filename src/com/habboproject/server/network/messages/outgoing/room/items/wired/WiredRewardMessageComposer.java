package com.habboproject.server.network.messages.outgoing.room.items.wired;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;


public class WiredRewardMessageComposer extends MessageComposer {
    private final int reason;

    public WiredRewardMessageComposer(final int reason) {
        this.reason = reason;
    }

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        // 1-5 = error
        // 6-7 = success (rewardMisc, rewardBadge)
        msg.writeInt(reason);
    }
}
