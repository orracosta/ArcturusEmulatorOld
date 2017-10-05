package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ModerationMuteUserMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int playerId = msg.readInt();
        final int muteLength = msg.readInt();

        // TODO: use length.

        if(!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();

            return;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if (session != null) {
            session.send(new AdvancedAlertMessageComposer(Locale.get("command.mute.muted")));
        }

        BanManager.getInstance().mute(playerId);
    }
}
