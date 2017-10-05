package com.habboproject.server.network.messages.outgoing.help;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;


public class TicketSentMessageComposer extends MessageComposer {
    public TicketSentMessageComposer() {

    }

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }
}
