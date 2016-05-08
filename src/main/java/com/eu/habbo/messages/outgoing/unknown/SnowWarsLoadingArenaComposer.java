package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:12.
 */
public class SnowWarsLoadingArenaComposer extends MessageComposer
{
    private final int count;

    public SnowWarsLoadingArenaComposer(int count)
    {
        this.count = count;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(3850);
        this.response.appendInt32(this.count); //GameID?
        this.response.appendInt32(0); //Count
            //this.response.appendInt(1); //ItemID to dispose?
        return this.response;
    }
}
