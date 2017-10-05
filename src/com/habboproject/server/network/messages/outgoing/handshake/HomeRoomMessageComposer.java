package com.habboproject.server.network.messages.outgoing.handshake;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class HomeRoomMessageComposer extends MessageComposer {
    private final int roomId;
    private final int newRoom;

    public HomeRoomMessageComposer(final int roomId, final int newRoom) {
        this.roomId = roomId;
        this.newRoom = newRoom;
    }

    @Override
    public short getId() {
        return Composers.NavigatorSettingsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.roomId);
        msg.writeInt(this.newRoom);
    }
}
