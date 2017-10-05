package com.habboproject.server.network.messages.incoming.quests;

import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class StartQuestMessageEvent implements com.habboproject.server.network.messages.incoming.Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int questId = msg.readInt();

        if (client.getPlayer().getQuests().hasStartedQuest(questId)) {
            // Already started it!
            return;
        }

        if (client.getPlayer().getData().getQuestId() != 0) {
            // We need to cancel their current one.
            if (!client.getPlayer().getQuests().hasCompletedQuest(client.getPlayer().getData().getQuestId())) {
                client.getPlayer().getQuests().cancelQuest(client.getPlayer().getData().getQuestId());
            }
        }

        final Quest quest = QuestManager.getInstance().getById(questId);

        if (quest == null) {
            return;
        }

        client.getPlayer().getQuests().startQuest(quest);
    }
}
