package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-10-2015 21:29.
 */
public class GuideSessionDetachedComposer extends MessageComposer
{
    //Used for feedback.
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuideSessionDetachedComposer);
        //Empty Body
        return this.response;
    }
}
