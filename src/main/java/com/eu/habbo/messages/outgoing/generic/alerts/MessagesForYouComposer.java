package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MessagesForYouComposer extends MessageComposer
{
    private String[] messages;

    public MessagesForYouComposer(String[] messages)
    {
        this.messages = messages;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MessagesForYouComposer);
        this.response.appendInt32(this.messages.length);

        for(String s : this.messages)
        {
            this.response.appendString(s);
        }

        return this.response;
    }
}
