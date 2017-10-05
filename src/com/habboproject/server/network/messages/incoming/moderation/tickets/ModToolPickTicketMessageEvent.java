package com.habboproject.server.network.messages.incoming.moderation.tickets;

import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.game.moderation.types.tickets.HelpTicketState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ModToolPickTicketMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        msg.readInt();
        int ticketId = msg.readInt();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();
            return;
        }

        final HelpTicket helpTicket = ModerationManager.getInstance().getTicket(ticketId);

        if (helpTicket == null) {
            return;
        } else if (helpTicket.getModeratorId() != 0) {
            client.send(new AlertMessageComposer("This ticket has already been picked by another moderator."));
            return;
        }

        helpTicket.setModeratorId(client.getPlayer().getId());
        helpTicket.setState(HelpTicketState.IN_PROGRESS);
        helpTicket.save();

        ModerationManager.getInstance().broadcastTicket(helpTicket);
    }
}
