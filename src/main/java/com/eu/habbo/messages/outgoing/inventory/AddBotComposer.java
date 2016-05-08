package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 15-10-2014 15:41.
 */
public class AddBotComposer extends MessageComposer
{
    private final Bot bot;

    public AddBotComposer(Bot bot)
    {
        this.bot = bot;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AddBotComposer);
        this.response.appendInt32(this.bot.getId());
        this.response.appendString(this.bot.getName());
        this.response.appendString(this.bot.getMotto());
        this.response.appendString(this.bot.getGender().toString().toLowerCase().charAt(0) + "");
        this.response.appendString(this.bot.getFigure());
        this.response.appendBoolean(true);
        return this.response;
    }
}
