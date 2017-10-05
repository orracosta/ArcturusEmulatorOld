package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class CreateRoomMessageComposer extends MessageComposer {
    private final int roomId;
    private final String roomName;

    public CreateRoomMessageComposer(final int roomId, final String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public short getId() {
        return Composers.FlatCreatedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.roomId);
        msg.writeString(this.roomName);
    }
}
