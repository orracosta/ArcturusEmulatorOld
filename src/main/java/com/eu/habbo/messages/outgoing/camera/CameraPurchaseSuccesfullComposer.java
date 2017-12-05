package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CameraPurchaseSuccesfullComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CameraPurchaseSuccesfullComposer);

        return this.response;
    }
}