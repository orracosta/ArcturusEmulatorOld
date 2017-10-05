package com.habboproject.server.network.messages.outgoing.moderation.tickets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class HelpTicketResponseMessageComposer extends MessageComposer {

    private int response;

    public HelpTicketResponseMessageComposer(final int response) {
        this.response = response;
    }

    @Override
    public short getId() {
        return Composers.ModeratorSupportTicketResponseMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.response);
        msg.writeString("");
    }
}
