package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.TagsComposer;

/**
 * Created on 21-11-2014 23:06.
 */
public class RequestTagsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new TagsComposer(Emulator.getGameEnvironment().getRoomManager().getTags()));
    }
}
