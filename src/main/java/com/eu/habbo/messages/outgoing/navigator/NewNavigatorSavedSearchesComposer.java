package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 15:19.
 */
public class NewNavigatorSavedSearchesComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorSavedSearchesComposer);
        this.response.appendInt32(4);

        this.response.appendInt32(1);
        this.response.appendString("official");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt32(2);
        this.response.appendString("recommended");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt32(3);
        this.response.appendString("my");
        this.response.appendString("");
        this.response.appendString("");

        this.response.appendInt32(4);
        this.response.appendString("favorites");
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}
