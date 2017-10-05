package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class GiveRespectMessageComposer extends MessageComposer {
    private final int playerId;
    private final int totalRespects;

    public GiveRespectMessageComposer(final int playerId, final int totalRespects) {
        this.playerId = playerId;
        this.totalRespects = totalRespects;
    }

    @Override
    public short getId() {
        return Composers.RespectNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeInt(totalRespects);
    }
}
