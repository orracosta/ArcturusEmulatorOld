package com.eu.habbo.messages.incoming.helper;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.helper.TalentTrackComposer;

/**
 * Created on 28-12-2014 23:27.
 */
public class RequestTalentTrackEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new TalentTrackComposer());
    }
}
