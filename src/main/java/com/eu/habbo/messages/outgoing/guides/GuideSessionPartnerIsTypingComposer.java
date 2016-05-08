package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-10-2015 21:46.
 */
public class GuideSessionPartnerIsTypingComposer extends MessageComposer
{
    private final boolean typing;

    public GuideSessionPartnerIsTypingComposer(boolean typing)
    {
        this.typing = typing;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuideSessionPartnerIsTypingComposer);
        this.response.appendBoolean(this.typing);
        return this.response;
    }
}
