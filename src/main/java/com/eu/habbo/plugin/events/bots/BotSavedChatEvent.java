package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;

import java.util.ArrayList;

public class BotSavedChatEvent extends BotEvent
{
    /**
     * Automatic Chatter.
     */
    public boolean autoChat;

    /**
     * Random Lines Selected.
     */
    public boolean randomChat;

    /**
     * Delay between chatter.
     */
    public int chatDelay;

    /**
     * Chatter.
     */
    public ArrayList<String> chat;

    /**
     * This event is triggered whenever the chat settings get modified.
     * Values can be altered to override behaviour.
     * @param bot The Bot this event applies to.
     * @param autoChat Automatic Chatter.
     * @param randomChat Random Lines Selected.
     * @param chatDelay Delay between chatter.
     * @param chat Chatter.
     */
    public BotSavedChatEvent(Bot bot, boolean autoChat, boolean randomChat, int chatDelay, ArrayList<String> chat)
    {
        super(bot);

        this.autoChat = autoChat;
        this.randomChat = randomChat;
        this.chatDelay = chatDelay;
        this.chat = chat;
    }
}
