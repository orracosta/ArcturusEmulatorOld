package com.habboproject.server.network.messages.incoming.help;

import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.game.moderation.types.tickets.HelpTicketState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.help.CallForHelpPendingCallsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class DeletePendingTicketMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final HelpTicket helpTicket = ModerationManager.getInstance().getActiveTicketByPlayerId(client.getPlayer().getId());

        if (helpTicket != null) {
            helpTicket.setState(HelpTicketState.CLOSED);
            helpTicket.save();

            ModerationManager.getInstance().broadcastTicket(helpTicket);

            ModerationManager.getInstance().getTickets().remove(helpTicket.getId());
            client.send(new CallForHelpPendingCallsMessageComposer(null));
        }
    }
}
