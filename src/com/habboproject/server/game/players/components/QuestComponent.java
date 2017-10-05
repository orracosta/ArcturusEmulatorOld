package com.habboproject.server.game.players.components;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.players.types.PlayerComponent;
import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.network.messages.outgoing.quests.QuestCompletedMessageComposer;
import com.habboproject.server.network.messages.outgoing.quests.QuestListMessageComposer;
import com.habboproject.server.network.messages.outgoing.quests.QuestStartedMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.storage.queries.quests.PlayerQuestsDao;
import org.apache.log4j.Logger;

import java.util.Map;

public class QuestComponent implements PlayerComponent {
    private static final Logger log = Logger.getLogger(QuestComponent.class.getName());

    private Player player;
    private Map<Integer, Integer> questProgression;

    public QuestComponent(Player player) {
        this.player = player;

        this.loadQuestProgression();
    }

    private void loadQuestProgression() {
        this.questProgression = PlayerQuestsDao.getQuestProgression(this.getPlayer().getId());
    }

    public boolean hasStartedQuest(int questId) {
        return this.questProgression.containsKey(questId);
    }

    public boolean hasCompletedQuest(int questId) {
        final Quest quest = QuestManager.getInstance().getById(questId);

        if (quest == null) return false;

        if (this.questProgression.containsKey(questId)) {
            if (this.questProgression.get(questId) >= quest.getGoalData()) {
                return true;
            }
        }

        return false;
    }

    public void startQuest(Quest quest) {
        if (this.questProgression.containsKey(quest.getId())) {
            // We've already started this quest
            return;
        }

        this.questProgression.put(quest.getId(), 0);
        PlayerQuestsDao.saveProgression(true, this.player.getId(), quest.getId(), 0);

        this.getPlayer().getSession().send(new QuestStartedMessageComposer(quest, this.getPlayer()));
        this.getPlayer().getSession().send(new QuestListMessageComposer(QuestManager.getInstance().getQuests(), this.getPlayer(), false));

        this.getPlayer().getData().setQuestId(quest.getId());
        this.getPlayer().getData().save();
    }

    public void cancelQuest(int questId) {
        PlayerQuestsDao.cancelQuest(questId, this.player.getId());
        this.questProgression.remove(questId);
    }

    public void progressQuest(QuestType type) {
        this.progressQuest(type, 0);
    }

    public void progressQuest(QuestType type, int data) {
        int questId = this.getPlayer().getData().getQuestId();

        if (questId == 0 || !this.hasStartedQuest(questId)) {
            return;
        }

        Quest quest = QuestManager.getInstance().getById(questId);

        if (quest == null) {
            return;
        }

        if (quest.getType() != type) {
            return;
        }

        if (this.hasCompletedQuest(questId)) {
            return;
        }

        int newProgressValue = this.getProgress(questId);

        switch (quest.getType()) {
            default:
                newProgressValue++;
                break;

            case EXPLORE_FIND_ITEM:
                if (quest.getGoalData() != data) {
                    return;
                }

                newProgressValue = quest.getGoalData();
                break;
        }

        if (newProgressValue >= quest.getGoalData()) {
            boolean refreshCreditBalance = false;
            boolean refreshCurrenciesBalance = false;

            try {
                switch (quest.getRewardType()) {
                    case ACTIVITY_POINTS:
                        this.getPlayer().getData().increaseActivityPoints(quest.getReward());
                        refreshCurrenciesBalance = true;
                        break;

                    case ACHIEVEMENT_POINTS:
                        this.getPlayer().getData().increaseAchievementPoints(quest.getReward());
                        this.getPlayer().sendNotif("Alert", Locale.get("game.received.achievementPoints").replace("%points%", quest.getReward() + ""));
                        this.getPlayer().poof();
                        break;

                    case VIP_POINTS:
                        this.player.getData().increasePoints(quest.getReward());
                        refreshCurrenciesBalance = true;
                        break;

                    case CREDITS:
                        this.getPlayer().getData().increaseCredits(quest.getReward());
                        refreshCreditBalance = true;
                        break;
                }

                if (!quest.getBadgeId().isEmpty()) {
                    // Deliver badge
                    this.player.getInventory().addBadge(quest.getBadgeId(), true);
                }
            } catch (Exception e) {
                log.error("Failed to deliver reward to player: " + this.getPlayer().getData().getUsername());
            }

            if (refreshCreditBalance) {
                this.getPlayer().getSession().send(this.getPlayer().composeCreditBalance());
            } else if (refreshCurrenciesBalance) {
                this.getPlayer().getSession().send(this.getPlayer().composeCurrenciesBalance());
                this.getPlayer().getSession().send(new UpdateActivityPointsMessageComposer(this.getPlayer().getData().getActivityPoints(), quest.getReward()));
            }

            this.getPlayer().getData().save();
        }

        if (this.questProgression.containsKey(questId)) {
            this.questProgression.replace(questId, newProgressValue);
        }

        PlayerQuestsDao.saveProgression(false, this.player.getId(), questId, newProgressValue);

        this.getPlayer().getSession().send(new QuestCompletedMessageComposer(quest, this.player));
        this.getPlayer().getSession().send(new QuestListMessageComposer(QuestManager.getInstance().getQuests(), this.player, false));
    }

    public int getProgress(int quest) {
        if (this.questProgression.containsKey(quest)) {
            return this.questProgression.get(quest);
        }

        return 0;
    }

    @Override
    public void dispose() {
        this.questProgression.clear();
        this.questProgression = null;

        this.player = null;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
