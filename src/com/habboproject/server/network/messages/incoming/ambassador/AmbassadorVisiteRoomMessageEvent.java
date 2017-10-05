package com.habboproject.server.network.messages.incoming.ambassador;

import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 31/01/2017.
 */
public class AmbassadorVisiteRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String playerName = msg.readString();

        if (!client.getPlayer().getPermissions().getRank().isAmbassador()) {
            return;
        }

        Session session = null;
        if ((session = NetworkManager.getInstance().getSessions().getByPlayerUsername(playerName)) == null) {
            return;
        }

        Player player = null;
        if ((player = session.getPlayer()) == null) {
            return;
        }

        if ((player.getEntity() == null) || (player.getEntity().getRoom() == null)) {
            return;
        }

        client.send(new RoomForwardMessageComposer(player.getEntity().getRoom().getId()));
    }
}
