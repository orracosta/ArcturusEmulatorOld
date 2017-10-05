package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;


public class UnreadMinimailsMessageComposer extends MessageComposer {

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        // TODO: Minimail
        msg.writeInt(0);
    }
}
