package com.habboproject.server.network.messages.outgoing.user.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;


public class CitizenshipStatusMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString("helper");
        msg.writeInt(4);
        msg.writeInt(4);

    }
}
