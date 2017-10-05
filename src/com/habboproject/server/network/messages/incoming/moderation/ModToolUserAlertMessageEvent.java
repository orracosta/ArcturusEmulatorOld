package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ModToolUserAlertMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int playerId = msg.readInt();
        String message = msg.readString();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            // fuck off
            client.getLogger().error(
                    ModToolUserInfoMessageEvent.class.getName() + " - tried to alert user: " + playerId + " with text: " + message);
            client.disconnect();
            return;
        }

        if (PlayerManager.getInstance().isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null) {
                session.send(new AlertMessageComposer(message));
            }
        }
    }
}
