package com.eu.habbo.messages.outgoing.gamcenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BaseJumpLoadGameURLComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BaseJumpLoadGameURLComposer);
        this.response.appendInt32(3);
        this.response.appendString("3002");
        this.response.appendString("127.0.0.1");
        return this.response;
    }
}