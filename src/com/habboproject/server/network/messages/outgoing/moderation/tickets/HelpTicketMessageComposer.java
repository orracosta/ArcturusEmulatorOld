package com.habboproject.server.network.messages.outgoing.moderation.tickets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class HelpTicketMessageComposer extends MessageComposer {
    private final HelpTicket helpTicket;

    public HelpTicketMessageComposer(HelpTicket helpTicket) {
        this.helpTicket = helpTicket;
        ;
    }

    @Override
    public short getId() {
        return Composers.ModeratorSupportTicketMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        this.helpTicket.compose(msg);
    }
}
