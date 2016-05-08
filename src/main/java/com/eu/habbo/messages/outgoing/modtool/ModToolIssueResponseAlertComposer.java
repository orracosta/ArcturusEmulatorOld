package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-7-2015 13:07.
 */
public class ModToolIssueResponseAlertComposer extends MessageComposer
{
    private String message;

    public ModToolIssueResponseAlertComposer(String message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueResponseAlertComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
