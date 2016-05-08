package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:16.
 */
public class SnowWarsResetTimerComposer extends MessageComposer
{
    //SnowStageRunning?
    @Override
    public ServerMessage compose()
    {
        this.response.init(294);
        this.response.appendInt32(100);
        return this.response;
    }
}
