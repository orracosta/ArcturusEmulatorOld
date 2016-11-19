package com.eu.habbo.messages.outgoing.habboway;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HabboWayQuizComposer1 extends MessageComposer
{
    public final String name;
    public final int[] items;

    public HabboWayQuizComposer1(String name, int[] items)
    {
        this.name = name;
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HabboWayQuizComposer1);
        this.response.appendString(this.name);
        this.response.appendInt32(this.items.length);
        for (int i = 0; i < this.items.length; i++)
        {
            this.response.appendInt32(this.items[i]);
        }
        return this.response;
    }
}