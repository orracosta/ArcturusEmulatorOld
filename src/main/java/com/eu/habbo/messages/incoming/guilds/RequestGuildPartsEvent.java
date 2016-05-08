package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildPartsComposer;

/**
 * Created on 22-11-2014 16:36.
 */
public class RequestGuildPartsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new GuildPartsComposer());
    }
}
