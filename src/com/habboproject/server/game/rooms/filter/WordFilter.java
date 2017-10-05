package com.habboproject.server.game.rooms.filter;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.storage.queries.filter.FilterDao;
import com.habboproject.server.utilities.FilterUtil;
import org.apache.log4j.Logger;

import java.util.Map;


public class WordFilter {
    private Map<String, String> wordfilter;

    public WordFilter() {
        this.loadFilter();
    }

    public void loadFilter() {
        if (this.wordfilter != null) {
            this.wordfilter.clear();
        }

        this.wordfilter = FilterDao.loadWordfilter();

        Logger.getLogger(WordFilter.class.getName()).info("Loaded " + wordfilter.size() + " filtered words");
    }

    public FilterResult filter(String message) {
        String filteredMessage = message;

        if (CometSettings.wordFilterMode == FilterMode.STRICT) {
            message = FilterUtil.process(message.toLowerCase());
        }

        for (Map.Entry<String, String> word : wordfilter.entrySet()) {
            if (message.toLowerCase().contains(word.getKey())) {
                if (CometSettings.wordFilterMode == FilterMode.STRICT)
                    return new FilterResult(true, word.getKey());

                filteredMessage = filteredMessage.replace("(?i)" + word.getKey(), word.getValue());
            }
        }

        return new FilterResult(filteredMessage, !message.equals(filteredMessage));
    }

    public void save() {
        FilterDao.save(this.wordfilter);
    }
}
