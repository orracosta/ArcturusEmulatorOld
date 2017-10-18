package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ItemStateComposer2 extends MessageComposer
{
    private final int id;
    private final int value;

    public ItemStateComposer2(int id, int value)
    {
        this.id = id;
        this.value = value;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ItemStateComposer2);
        this.response.appendInt(this.id);
        this.response.appendInt(this.value);
        return this.response;
    }
}