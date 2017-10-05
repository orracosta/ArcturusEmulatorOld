package com.habboproject.server.game.quests;

import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.storage.queries.quests.QuestsDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.Map;

public class QuestManager implements Initializable {
    private static final Logger log = Logger.getLogger(QuestManager.class.getName());

    private static QuestManager questManagerInstance;

    private Map<String, Quest> quests;

    public QuestManager() {

    }

    @Override
    public void initialize() {
        this.loadQuests();
    }

    public void loadQuests() {
        if (this.quests != null) {
            this.quests.clear();
        }

        this.quests = QuestsDao.getAllQuests();
        log.info("Loaded " + this.quests.size() + " quests");
        log.info("QuestManager initialized");
    }

    public static QuestManager getInstance() {
        if (questManagerInstance == null) {
            questManagerInstance = new QuestManager();
        }

        return questManagerInstance;
    }

    public Quest getById(int questId) {
        for (Quest quest : this.quests.values()) {
            if (quest.getId() == questId)
                return quest;
        }

        return null;
    }

    public int getAmountOfQuestsInCategory(String category) {
        int count = 0;

        for (Quest quest : this.quests.values()) {
            if (quest.getCategory().equals(category)) {
                count++;
            }
        }

        return count;
    }

    public Quest getNextQuestInSeries(Quest lastQuest) {
        for (Quest quest : this.quests.values()) {
            if (quest.getCategory().equals(lastQuest.getCategory()) &&
                    quest.getSeriesNumber() == (lastQuest.getSeriesNumber() + 1)) {
                return quest;
            }
        }

        return null;
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }
}
