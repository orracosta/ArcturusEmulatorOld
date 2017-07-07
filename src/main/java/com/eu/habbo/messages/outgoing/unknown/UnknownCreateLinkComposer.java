package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownCreateLinkComposer extends MessageComposer
{
    private final String link;

    public UnknownCreateLinkComposer(String link)
    {
        this.link = link;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownCreateLinkComposer);
        this.response.appendString(this.link);
        return this.response;
    }
}