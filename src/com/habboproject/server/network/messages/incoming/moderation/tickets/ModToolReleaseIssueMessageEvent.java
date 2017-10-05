package com.habboproject.server.network.messages.incoming.moderation.tickets;

import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.game.moderation.types.tickets.HelpTicketState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ModToolReleaseIssueMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int ticketCount = msg.readInt();
        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();
            return;
        }

        for (int i = 0; i < ticketCount; i++) {
            int ticketId = msg.readInt();

            final HelpTicket helpTicket = ModerationManager.getInstance().getTicket(ticketId);

            if (helpTicket == null || helpTicket.getModeratorId() != client.getPlayer().getId()) return;

            helpTicket.setState(HelpTicketState.OPEN);
            helpTicket.setModeratorId(0);

            helpTicket.save();

            ModerationManager.getInstance().broadcastTicket(helpTicket);
        }
    }
}
