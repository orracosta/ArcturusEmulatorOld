package com.habboproject.server.network.messages.incoming.handshake;

import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SSOTicketMessageEvent implements Event {
    public static final String TICKET_DELIMITER = ":";

    public void handle(Session client, MessageEvent msg) {
        if (BanManager.getInstance().hasBan(client.getUniqueId(), BanType.MACHINE)) {
            client.getLogger().warn("Banned player: " + client.getUniqueId() + " tried logging in");
            return;
        }

        String ticket = msg.readString();

        if (ticket.isEmpty()) {
            client.disconnect();
            return;
        }

        PlayerManager.getInstance().submitLoginRequest(client, ticket);
    }
}
