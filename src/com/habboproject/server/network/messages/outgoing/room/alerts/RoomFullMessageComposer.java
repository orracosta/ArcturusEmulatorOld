package com.habboproject.server.network.messages.outgoing.room.alerts;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomFullMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.RoomErrorNotifMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeString("/x363");

    }
}
