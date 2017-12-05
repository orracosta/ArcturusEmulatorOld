package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class VerifyMobileNumberComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.VerifyMobileNumberComposer);
        //Empty body
        return this.response;
    }
}