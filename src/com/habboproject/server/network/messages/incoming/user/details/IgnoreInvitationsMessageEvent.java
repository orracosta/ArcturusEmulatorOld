package com.habboproject.server.network.messages.incoming.user.details;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class IgnoreInvitationsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        boolean ignoreRoomInvitations = msg.readBoolean();

        client.getPlayer().getSettings().setIgnoreInvites(ignoreRoomInvitations);
        PlayerDao.saveIgnoreInvitations(ignoreRoomInvitations, client.getPlayer().getId());
    }
}
