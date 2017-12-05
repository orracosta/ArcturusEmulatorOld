package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HelperRequestDisabledComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HelperRequestDisabledComposer);
        this.response.appendString("");
        return this.response;
    }
}
