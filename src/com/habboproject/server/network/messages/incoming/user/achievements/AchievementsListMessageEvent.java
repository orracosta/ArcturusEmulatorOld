package com.habboproject.server.network.messages.incoming.user.achievements;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementsListMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class AchievementsListMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new AchievementsListMessageComposer(client.getPlayer().getAchievements()));
    }
}
