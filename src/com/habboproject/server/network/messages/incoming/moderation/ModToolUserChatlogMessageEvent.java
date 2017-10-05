package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.game.moderation.chatlog.UserChatlogContainer;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.database.queries.LogQueries;
import com.habboproject.server.logging.entries.RoomVisitLogEntry;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.moderation.ModToolUserChatlogMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ModToolUserChatlogMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int userId = msg.readInt();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            return;
        }

        if (!LogManager.ENABLED)
            client.send(new AdvancedAlertMessageComposer("Notice", "Logging is not currently enabled, please contact your system administrator to enable it."));

        UserChatlogContainer chatlogContainer = new UserChatlogContainer();

        for (RoomVisitLogEntry visit : LogQueries.getLastRoomVisits(userId, 50)) {
            chatlogContainer.addAll(visit.getRoomId(), LogQueries.getChatlogsByCriteria(visit.getPlayerId(), visit.getRoomId(), visit.getEntryTime(), visit.getExitTime()));
        }

        client.send(new ModToolUserChatlogMessageComposer(userId, chatlogContainer));
    }
}
