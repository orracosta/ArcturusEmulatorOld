package com.habboproject.server.network.messages.outgoing.help;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class CallForHelpPendingCallsMessageComposer extends MessageComposer {
    private final HelpTicket helpTicket;

    public CallForHelpPendingCallsMessageComposer(HelpTicket helpTicket) {
        this.helpTicket = helpTicket;
    }

    @Override
    public short getId() {
        return Composers.CallForHelpPendingCallsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.helpTicket == null ? 0 : 1);

        if (this.helpTicket != null) {
            msg.writeString(this.helpTicket.getId());
            msg.writeString(this.helpTicket.getDateSubmitted());
            msg.writeString(this.helpTicket.getMessage());
        }
    }
}
