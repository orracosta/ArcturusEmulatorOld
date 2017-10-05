package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class IdleStatusMessageComposer extends MessageComposer {
    private final int playerId;
    private final boolean isIdle;

    public IdleStatusMessageComposer(final int playerId, final boolean isIdle) {
        this.playerId = playerId;
        this.isIdle = isIdle;
    }

    @Override
    public short getId() {
        return Composers.SleepMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerId);
        msg.writeBoolean(this.isIdle);
    }
}
