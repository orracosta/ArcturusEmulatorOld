package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.database.queries.LogQueries;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.moderation.ModToolRoomVisitsMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.google.common.collect.Lists;


public class ModToolRoomVisitsMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int playerId = msg.readInt();

        if (!LogManager.ENABLED) {
            client.send(new AdvancedAlertMessageComposer("Notice", "Logging is not currently enabled, please contact your system administrator to enable it."));
            client.send(new ModToolRoomVisitsMessageComposer(playerId, PlayerDao.getUsernameByPlayerId(playerId), Lists.newArrayList()));
        }

        if (LogManager.ENABLED)
            client.send(new ModToolRoomVisitsMessageComposer(playerId, PlayerDao.getUsernameByPlayerId(playerId), LogQueries.getLastRoomVisits(playerId, 100)));
    }
}
