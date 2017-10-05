package com.habboproject.server.network.messages.outgoing.quests;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class QuestListMessageComposer extends MessageComposer {
    private final Map<String, Quest> quests;
    private final Player player;
    private boolean isWindow;

    public QuestListMessageComposer(final Map<String, Quest> quests, Player player, boolean isWindow) {
        this.quests = quests;
        this.player = player;
        this.isWindow = isWindow;
    }

    @Override
    public short getId() {
        return Composers.QuestListMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        Map<String, Quest> categoryCounters = Maps.newHashMap();

        List<Quest> activeQuests = Lists.newArrayList();
        List<Quest> inactiveQuests = Lists.newArrayList();

        try {
            for (Quest quest : this.quests.values()) {
                if (categoryCounters.containsKey(quest.getCategory())) {
                    if (!this.player.getQuests().hasCompletedQuest(quest.getId())) {
                        if (!this.player.getQuests().hasCompletedQuest(categoryCounters.get(quest.getCategory()).getId())) {
                            if (categoryCounters.get(quest.getCategory()).getSeriesNumber() > quest.getSeriesNumber()) {
                                categoryCounters.replace(quest.getCategory(), quest);
                            }
                        } else {
                            if (categoryCounters.get(quest.getCategory()).getSeriesNumber() < quest.getSeriesNumber()) {
                                categoryCounters.replace(quest.getCategory(), quest);
                            }
                        }
                    } else {
                        if (quest.getSeriesNumber() > categoryCounters.get(quest.getCategory()).getSeriesNumber()) {
                            categoryCounters.replace(quest.getCategory(), quest);
                        }
                    }
                } else {
                    categoryCounters.put(quest.getCategory(), quest);
                }
            }

            for (Quest quest : categoryCounters.values()) {
                if (this.player.getQuests().hasCompletedQuest(quest.getId())) {
                    inactiveQuests.add(quest);
                } else {
                    activeQuests.add(quest);
                }
            }

            msg.writeInt(activeQuests.size() + inactiveQuests.size());

            for (Quest activeQuest : activeQuests) {
                composeQuest(activeQuest.getCategory(), activeQuest, msg);
            }

            for (Quest inactiveQuest : inactiveQuests) {
                composeQuest(inactiveQuest.getCategory(), null, msg);
            }

            msg.writeBoolean(this.isWindow);  // send ??
        } finally {
            categoryCounters.clear();

            inactiveQuests.clear();
            activeQuests.clear();
        }
    }

    private void composeQuest(final String category, final Quest quest, final IComposer msg) {
        int amountInCategory = QuestManager.getInstance().getAmountOfQuestsInCategory(category);

        if (quest == null) {
            msg.writeString(category);
            msg.writeInt(amountInCategory);
            msg.writeInt(amountInCategory);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeBoolean(false);
            msg.writeString("");
            msg.writeString("");
            msg.writeInt(0);
            msg.writeString("");
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeString("");
            msg.writeString("");
            msg.writeBoolean(true);// easy
            return;
        }

        quest.compose(this.player, msg);
    }
}
