package com.habboproject.server.network.messages.outgoing.room.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class FloodFilterMessageComposer extends MessageComposer {
    private final double seconds;

    public FloodFilterMessageComposer(double seconds) {
        this.seconds = seconds;
    }

    @Override
    public short getId() {
        return Composers.FloodControlMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(((int) Math.round(this.seconds)));
    }
}
