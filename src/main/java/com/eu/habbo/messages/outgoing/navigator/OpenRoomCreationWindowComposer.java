package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

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
