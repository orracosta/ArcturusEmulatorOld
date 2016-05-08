package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.HabboGender;

/**
 * Created on 24-10-2015 13:11.
 */
public class BotSavedLookEvent extends BotEvent
{
    /**
     * The new gender of this bot. Can be altered.
     */
    public HabboGender gender;

    /**
     * The new look of this bot. Can be altered.
     */
    public String newLook;

    /**
     * @param bot The Bot this event applies to.
     * @param gender The new gender of this bot. Can be altered.
     * @param newLook The new look of this bot. Can be altered.
     */
    public BotSavedLookEvent(Bot bot, HabboGender gender, String newLook)
    {
        super(bot);

        this.gender = gender;
        this.newLook = newLook;
    }
}
