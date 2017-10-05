package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomEntryInfoMessageComposer extends MessageComposer {
    private final int id;
    private final boolean hasOwnershipPermission;

    public RoomEntryInfoMessageComposer(final int id, final boolean hasOwnershipPermission) {
        this.id = id;
        this.hasOwnershipPermission = hasOwnershipPermission;
    }

    @Override
    public short getId() {
        return Composers.RoomEntryInfoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(id);
        msg.writeBoolean(hasOwnershipPermission);
    }
}
