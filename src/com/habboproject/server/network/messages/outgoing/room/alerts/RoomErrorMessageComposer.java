package com.habboproject.server.network.messages.outgoing.room.alerts;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class RoomErrorMessageComposer extends MessageComposer {
    private final int errorCode;

    public RoomErrorMessageComposer(final int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public short getId() {
        return Composers.GenericErrorMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.errorCode);
    }
}
