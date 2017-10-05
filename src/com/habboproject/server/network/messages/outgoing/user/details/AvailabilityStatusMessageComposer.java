package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class AvailabilityStatusMessageComposer extends MessageComposer {

    @Override
    public short getId() {
        return Composers.AvailabilityStatusMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(false);
        msg.writeBoolean(false);
        msg.writeBoolean(true);
    }
}
