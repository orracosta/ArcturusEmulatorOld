package com.habboproject.server.network.messages.outgoing.handshake;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class UniqueMachineIDMessageComposer extends MessageComposer {
    private final String uniqueId;

    public UniqueMachineIDMessageComposer(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public short getId() {
        return Composers.UniqueMachineIDMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.uniqueId);
    }
}
