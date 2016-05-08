package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-3-2015 20:29.
 */
public class UpdateFailedComposer extends MessageComposer
{
    private final String message;

    public UpdateFailedComposer(String message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateFailedComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
