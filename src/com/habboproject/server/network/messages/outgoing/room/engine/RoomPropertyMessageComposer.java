package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomPropertyMessageComposer extends MessageComposer {
    private final String key;
    private final String value;

    public RoomPropertyMessageComposer(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public short getId() {
        return Composers.RoomPropertyMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.key);
        msg.writeString(this.value);
    }
}
