package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class HabboNameChangedComposer extends MessageComposer
{
    private final int unknownInt;
    private final String name;
    private final List<String> unknownStringList;

    public HabboNameChangedComposer(int unknownInt, String name, List<String> unknownStringList)
    {
        this.unknownInt = unknownInt;
        this.name = name;
        this.unknownStringList = unknownStringList;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HabboNameChangedComposer);
        this.response.appendInt(this.unknownInt);
        this.response.appendString(this.name);
        this.response.appendInt(this.unknownStringList.size());
        for (String s : this.unknownStringList)
        {
            this.response.appendString(s);
        }

        return this.response;
    }
}