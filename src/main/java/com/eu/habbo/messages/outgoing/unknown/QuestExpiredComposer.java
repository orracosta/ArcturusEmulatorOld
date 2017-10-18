package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuestExpiredComposer extends MessageComposer
{
    private final boolean expired;

    public QuestExpiredComposer(boolean expired)
    {
        this.expired = expired;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.QuestExpiredComposer);
        this.response.appendBoolean(this.expired);
        return this.response;
    }
}