package com.eu.habbo.messages.outgoing.gamcenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GameCenterAccountInfoComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameCenterAccountInfoComposer);
        this.response.appendInt(3);
        this.response.appendInt(2);
        this.response.appendInt(1);
        return this.response;
    }
}