package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CfhTopicsMessageComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(325);
        this.response.appendInt32(1);
        this.response.appendString("sexual_content");
        this.response.appendInt32(2);
        this.response.appendString("explicit_sexual_talk");
        this.response.appendInt32(1);
        this.response.appendString("mods");
        this.response.appendString("cybersex");
        this.response.appendInt32(2);
        this.response.appendString("mods");
        return this.response;
    }
}