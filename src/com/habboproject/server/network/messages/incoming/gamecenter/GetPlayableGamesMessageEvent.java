package com.habboproject.server.network.messages.incoming.gamecenter;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.gamecenter.PlayableGamesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 05/03/2017.
 */
public class GetPlayableGamesMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new PlayableGamesMessageComposer(msg.readInt()));
    }
}
