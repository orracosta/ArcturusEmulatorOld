package com.habboproject.server.network.messages.incoming.quests;

import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class NextQuestMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getData().getQuestId() == 0) {
            return;
        }

        Quest quest = QuestManager.getInstance().getById(client.getPlayer().getData().getQuestId());

        if (quest == null) {
            return;
        }

        if (!client.getPlayer().getQuests().hasCompletedQuest(quest.getId())) {
            return;
        }

        Quest nextQuest = QuestManager.getInstance().getNextQuestInSeries(quest);

        if (nextQuest != null) {
            client.getPlayer().getQuests().startQuest(nextQuest);
        }
    }
}
