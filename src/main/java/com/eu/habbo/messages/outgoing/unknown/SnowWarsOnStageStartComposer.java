package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsOnStageStartComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(889);
        this.response.appendInt32(0);
        this.response.appendString("snowwar_arena_0");
        this.response.appendInt32(0);
        this.response.appendInt32(2); //Count

        this.response.appendInt32(5);
        this.response.appendInt32(1);
        this.response.appendInt32(64000);
        this.response.appendInt32(64000);
        this.response.appendInt32(20);
        this.response.appendInt32(24);
        this.response.appendInt32(1);
        this.response.appendInt32(100);
        this.response.appendInt32(4);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(20);
        this.response.appendInt32(24);
        this.response.appendInt32(64000);
        this.response.appendInt32(64000);
        this.response.appendInt32(0);
        this.response.appendInt32(1);
        this.response.appendInt32(1);
        this.response.appendString("Admin");
        this.response.appendString("Motto");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");

        this.response.appendInt32(5);
        this.response.appendInt32(2);
        this.response.appendInt32(64000);
        this.response.appendInt32(64000);
        this.response.appendInt32(20);
        this.response.appendInt32(24);
        this.response.appendInt32(1);
        this.response.appendInt32(100);
        this.response.appendInt32(4);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(20);
        this.response.appendInt32(24);
        this.response.appendInt32(64000);
        this.response.appendInt32(64000);
        this.response.appendInt32(0);
        this.response.appendInt32(2);
        this.response.appendInt32(2);
        this.response.appendString("Admin");
        this.response.appendString("Motto");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");

        return this.response;
    }
}
