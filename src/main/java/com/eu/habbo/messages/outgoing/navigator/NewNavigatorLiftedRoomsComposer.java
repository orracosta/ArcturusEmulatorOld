package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 15:15.
 */
public class NewNavigatorLiftedRoomsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorLiftedRoomsComposer);
        this.response.appendInt32(0);
        return this.response;
    }
}
