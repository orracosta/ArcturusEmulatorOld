package com.habboproject.server.network.messages.outgoing.misc;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class PingMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.LatencyResponseMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }
}
