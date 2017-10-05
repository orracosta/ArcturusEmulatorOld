package com.habboproject.server.network.messages.incoming.moderation.tickets;

import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.logging.database.queries.LogQueries;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.moderation.tickets.ModToolTicketChatlogMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ModToolTicketChatlogMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();
            return;
        }

        final int ticketId = msg.readInt();
        final HelpTicket helpTicket = ModerationManager.getInstance().getTicket(ticketId);

        if (helpTicket == null || helpTicket.getModeratorId() != client.getPlayer().getId()) {
            // Doesn't exist or already picked!
            return;
        }

        final RoomData roomData = RoomManager.getInstance().getRoomData(helpTicket.getRoomId());

        if (roomData == null) return;

        client.send(new ModToolTicketChatlogMessageComposer(helpTicket, helpTicket.getRoomId(), roomData.getName(), LogQueries.getChatlogsForRoom(roomData.getId(), helpTicket.getDateSubmitted() - (30 * 60), helpTicket.getDateSubmitted() + (10 * 60))));
    }
}
