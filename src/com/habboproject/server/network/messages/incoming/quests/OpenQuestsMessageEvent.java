package com.habboproject.server.network.messages.incoming.quests;

import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.quests.QuestListMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class OpenQuestsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new QuestListMessageComposer(QuestManager.getInstance().getQuests(), client.getPlayer(), true));
    }
}
