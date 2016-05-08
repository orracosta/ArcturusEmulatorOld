package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 22:33.
 */
public class SnowWarsQuickJoinComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(913);
        this.response.appendInt32(1);
        this.response.appendString("SnowStorm level " + 9);
        this.response.appendInt32(0); //Stage ID
        this.response.appendInt32(9);
        this.response.appendInt32(2);
        this.response.appendInt32(10);
        this.response.appendString("Admin");
        this.response.appendInt32(0);
        this.response.appendInt32(2);
        this.response.appendInt32(1);
        this.response.appendString("Admin");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");
        this.response.appendInt32(-1);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(2);
        this.response.appendString("Droppy");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");
        this.response.appendInt32(-1);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        return this.response;
    }
}
