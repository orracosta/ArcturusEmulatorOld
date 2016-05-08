package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 16-11-2015 22:32.
 */
public class SimplePollStartComposer extends MessageComposer
{
    public final String question;

    public SimplePollStartComposer(String question)
    {
        this.question = question;
    }
    //:test 3047 s:a i:10 i:20 i:10000 i:1 i:1 i:3 s:abcdefghijklmnopqrstuvwxyz12345678901234? i:1 s:a s:b

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.SimplePollStartComposer);
        this.response.appendString("");
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        return null;
    }
}
