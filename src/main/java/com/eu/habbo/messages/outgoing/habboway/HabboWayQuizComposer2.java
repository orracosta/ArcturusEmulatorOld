package com.eu.habbo.messages.outgoing.habboway;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HabboWayQuizComposer2 extends MessageComposer
{
    public final String name;
    public final int[] items;

    public HabboWayQuizComposer2(String name, int[] items)
    {
        this.name = name;
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HabboWayQuizComposer2);
        this.response.appendString(this.name);
        this.response.appendInt(this.items.length);
        for (int i = 0; i < this.items.length; i++)
        {
            this.response.appendInt(this.items[i]);
        }
        return this.response;
    }
}