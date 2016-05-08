package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 11-10-2015 16:43.
 */
public class GuardianNewReportReceivedComposer extends MessageComposer
{
    private final GuardianTicket ticket;

    public GuardianNewReportReceivedComposer(GuardianTicket ticket)
    {
        this.ticket = ticket;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuardianNewReportReceivedComposer);
        this.response.appendInt32(Emulator.getConfig().getInt("guardians.accept.timer"));
        return this.response;
    }
}
