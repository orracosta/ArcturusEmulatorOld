package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Map;

public class SimplePollAnswersComposer extends MessageComposer
{
    private final int unknownInt;
    private final int no;
    private final int yes;

    public SimplePollAnswersComposer(int unknownInt, int no, int yes)
    {
        this.unknownInt = unknownInt;
        this.no = no;
        this.yes = yes;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.SimplePollAnswersComposer);
        this.response.appendInt(-1);
        this.response.appendInt(2);
        this.response.appendString("0");
        this.response.appendInt(this.no);
        this.response.appendString("1");
        this.response.appendInt(this.yes);
        return this.response;
    }
}