package com.habboproject.server.network.messages.incoming.gamecenter;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.gamecenter.GameAccountStatusMessageComposer;
import com.habboproject.server.network.messages.outgoing.gamecenter.GameListMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 08/02/2017.
 */
public class GetGameListMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() != null && client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            client.getPlayer().getEntity().leaveRoom(false, false, true);
        }

        client.send(new GameListMessageComposer());
    }
}
