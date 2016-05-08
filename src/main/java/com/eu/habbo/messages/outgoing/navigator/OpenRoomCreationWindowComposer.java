package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 16-11-2015 22:30.
 */
public class OpenRoomCreationWindowComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.OpenRoomCreationWindowComposer);
        //Empty Body
        return this.response;
    }
}
