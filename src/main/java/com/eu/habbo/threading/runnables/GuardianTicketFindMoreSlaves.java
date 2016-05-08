package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuardianTicket;

/**
 * Created on 17-10-2015 18:53.
 */
public class GuardianTicketFindMoreSlaves implements Runnable
{
    private final GuardianTicket ticket;

    public GuardianTicketFindMoreSlaves(GuardianTicket ticket)
    {
        this.ticket = ticket;
    }

    @Override
    public void run()
    {
        Emulator.getGameEnvironment().getGuideManager().findGuardians(ticket);
    }
}
