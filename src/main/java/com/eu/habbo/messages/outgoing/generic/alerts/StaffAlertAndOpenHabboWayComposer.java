package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class StaffAlertAndOpenHabboWayComposer extends MessageComposer
{
    private String message;

    public StaffAlertAndOpenHabboWayComposer(String message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.StaffAlertAndOpenHabboWayComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
