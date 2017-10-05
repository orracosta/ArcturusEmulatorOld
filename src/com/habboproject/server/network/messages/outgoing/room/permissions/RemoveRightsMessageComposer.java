package com.habboproject.server.network.messages.outgoing.room.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RemoveRightsMessageComposer extends MessageComposer {
    private final int playerId;
    private final int roomId;

    public RemoveRightsMessageComposer(final int playerId, final int roomId) {
        this.playerId = playerId;
        this.roomId = roomId;
    }

    @Override
    public short getId() {
        return Composers.FlatControllerRemovedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(roomId);
        msg.writeInt(playerId);
    }
}
