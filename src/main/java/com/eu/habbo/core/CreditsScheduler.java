package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

/**
 * Created on 9-7-2015 15:08.
 */
public class CreditsScheduler implements Runnable
{
    public static boolean IGNORE_HOTEL_VIEW;
    public static boolean IGNORE_IDLED;

    public static int CREDITS;
    public static int INTERVAL;

    public boolean disposed = false;

    public CreditsScheduler()
    {
        if(Emulator.getConfig().getBoolean("hotel.auto.credits.enabled"))
        {
            IGNORE_HOTEL_VIEW   = Emulator.getConfig().getBoolean("hotel.auto.credits.ignore.hotelview");
            IGNORE_IDLED        = Emulator.getConfig().getBoolean("hotel.auto.credits.ignore.idled");

            CREDITS             = Emulator.getConfig().getInt("hotel.auto.credits.amount");
            INTERVAL            = Emulator.getConfig().getInt("hotel.auto.credits.interval");

            Emulator.getThreading().run(this);
        }
    }

    @Override
    public void run()
    {
        if(this.disposed)
            return;

        Emulator.getThreading().run(this, INTERVAL * 1000);

        Habbo habbo;
        for(Map.Entry<Integer, Habbo> map : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
        {
            habbo = map.getValue();

            try
            {
                if (habbo != null)
                {
                    if (habbo.getHabboInfo().getCurrentRoom() == null && IGNORE_HOTEL_VIEW)
                        continue;

                    if (habbo.getRoomUnit().isIdle() && IGNORE_IDLED)
                        continue;

                    habbo.giveCredits(CREDITS);
                }
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public boolean isDisposed()
    {
        return disposed;
    }

    public void setDisposed(boolean disposed)
    {
        this.disposed = disposed;
    }
}
