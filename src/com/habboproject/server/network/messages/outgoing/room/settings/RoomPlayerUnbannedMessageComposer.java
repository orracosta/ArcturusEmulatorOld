package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomPlayerUnbannedMessageComposer extends MessageComposer {
    private final int roomId;
    private final int playerId;

    public RoomPlayerUnbannedMessageComposer(int roomId, int playerId) {
        this.roomId = roomId;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.UnbanUserFromRoomMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(roomId);
        msg.writeInt(playerId);
    }
}
