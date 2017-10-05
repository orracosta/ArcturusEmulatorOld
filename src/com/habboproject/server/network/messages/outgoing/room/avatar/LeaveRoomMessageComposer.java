package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class LeaveRoomMessageComposer extends MessageComposer {
    private final int playerId;

    public LeaveRoomMessageComposer(final int playerId) {
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.UserRemoveMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(playerId);
    }
}
