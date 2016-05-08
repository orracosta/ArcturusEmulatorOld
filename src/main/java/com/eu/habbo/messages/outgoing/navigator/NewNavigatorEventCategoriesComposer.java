package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NewNavigatorEventCategoriesComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorEventCategoriesComposer);
        this.response.appendInt32(11);
        this.response.appendInt32(1);
        this.response.appendString("Hottest Events");
        this.response.appendBoolean(false);
        this.response.appendInt32(2);
        this.response.appendString("Parties & Music");
        this.response.appendBoolean(true);
        this.response.appendInt32(3);
        this.response.appendString("Role Play");
        this.response.appendBoolean(true);
        this.response.appendInt32(4);
        this.response.appendString("Help Desk");
        this.response.appendBoolean(true);
        this.response.appendInt32(5);
        this.response.appendString("Trading");
        this.response.appendBoolean(true);
        this.response.appendInt32(6);
        this.response.appendString("Games");
        this.response.appendBoolean(true);
        this.response.appendInt32(7);
        this.response.appendString("Debates & Discussions");
        this.response.appendBoolean(true);
        this.response.appendInt32(8);
        this.response.appendString("Grand Openings");
        this.response.appendBoolean(true);
        this.response.appendInt32(9);
        this.response.appendString("Friending");
        this.response.appendBoolean(true);
        this.response.appendInt32(10);
        this.response.appendString("Jobs");
        this.response.appendBoolean(true);
        this.response.appendInt32(11);
        this.response.appendString("Group Events");
        this.response.appendBoolean(true);
        return this.response;
    }
}
