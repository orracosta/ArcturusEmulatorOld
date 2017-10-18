package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class StaffAlertWithLinkComposer extends MessageComposer
{
    private String message;
    private String link;

    public StaffAlertWithLinkComposer(String message, String link)
    {
        this.message = message;
        this.link = link;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.StaffAlertWithLinkComposer);
        this.response.appendString(this.message);
        this.response.appendString(this.link);
        return this.response;
    }
}
