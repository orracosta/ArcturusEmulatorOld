package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.modtool.WordFilterWord;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 25-12-2015 16:48.
 */
public class UserTriggerWordFilterEvent extends UserEvent
{
    /**
     * The word that triggered the filter.
     */
    public final WordFilterWord word;

    /**
     * @param habbo The Habbo this event applies to.
     * @param word The word that triggered the filter.
     */
    public UserTriggerWordFilterEvent(Habbo habbo, WordFilterWord word)
    {
        super(habbo);

        this.word = word;
    }
}
