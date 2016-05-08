package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.guides.GuardianTicket;

/**
 * Created on 11-10-2015 17:46.
 */
public class GuardianVotingFinish implements Runnable
{
    private final GuardianTicket ticket;
    private int checkSum = 0;

    public GuardianVotingFinish(GuardianTicket ticket)
    {
        this.ticket = ticket;
        this.checkSum = this.ticket.getCheckSum();
    }

    @Override
    public void run()
    {
        if(!this.ticket.isFinished())
        {
            if(this.ticket.getCheckSum() == this.checkSum)
            {
                this.ticket.finish();
            }
        }
    }
}
