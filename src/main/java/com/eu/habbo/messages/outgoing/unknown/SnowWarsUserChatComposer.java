package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:10.
 */
public class SnowWarsUserChatComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2049);
        this.response.appendInt32(1); //UserID
        this.response.appendString("Message");
        return this.response;
    }
}
