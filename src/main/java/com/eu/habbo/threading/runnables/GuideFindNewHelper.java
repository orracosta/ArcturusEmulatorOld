package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;

/**
 * Created on 10-10-2015 23:05.
 */
public class GuideFindNewHelper implements Runnable
{
    private final GuideTour tour;
    private final int checkSum;

    public GuideFindNewHelper(GuideTour tour)
    {
        this.tour = tour;
        this.checkSum = tour.checkSum;
    }

    @Override
    public void run()
    {
        if(!this.tour.isEnded() && this.tour.checkSum == this.checkSum && this.tour.getHelper() == null)
        {
            Emulator.getGameEnvironment().getGuideManager().findHelper(this.tour);
        }
    }
}
