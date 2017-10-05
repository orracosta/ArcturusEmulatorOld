package com.habboproject.server.network.messages.incoming.ambassador;

import com.habboproject.server.config.Locale;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 31/01/2017.
 */
public class AmbassadorSendAlertToPlayerMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int playerId = msg.readInt();

        if (!client.getPlayer().getPermissions().getRank().isAmbassador()) {
            return;
        }

        Session session = null;
        if ((session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId)) == null) {
            return;
        }

        session.send(new
                AdvancedAlertMessageComposer(Locale.get("ambassador.warning.title"),
                Locale.get("ambassador.warning.message").replace("%username%",
                        session.getPlayer().getData().getUsername())));
    }
}
