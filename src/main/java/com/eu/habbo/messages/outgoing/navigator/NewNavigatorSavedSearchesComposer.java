package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NewNavigatorSavedSearchesComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorSavedSearchesComposer);
        this.response.appendInt(4);

        this.response.appendInt(1);
        this.response.appendString("official");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt(2);
        this.response.appendString("recommended");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt(3);
        this.response.appendString("my");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt(4);
        this.response.appendString("favorites");
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}
