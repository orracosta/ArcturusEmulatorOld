package com.habboproject.server.network.messages.incoming.gamecenter;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.gamecenter.GameAchievementsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 03/03/2017.
 */
public class GetGameAchievementsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new GameAchievementsMessageComposer());
    }
}
