package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

public class CreditsScheduler extends Scheduler
{
    /**
     * Defines if users that are not in a room should be excluded from receiving credits.
     */
    public static boolean IGNORE_HOTEL_VIEW;

    /**
     * Defines if users idling in rooms should be excluded from receiving credits.
     */
    public static boolean IGNORE_IDLED;

    /**
     * The amount of credits that will be given.
     */
    public static int CREDITS;

    public CreditsScheduler()
    {
        super(Emulator.getConfig().getInt("hotel.auto.credits.interval"));

        if(Emulator.getConfig().getBoolean("hotel.auto.credits.enabled"))
        {
            IGNORE_HOTEL_VIEW   = Emulator.getConfig().getBoolean("hotel.auto.credits.ignore.hotelview");
            IGNORE_IDLED        = Emulator.getConfig().getBoolean("hotel.auto.credits.ignore.idled");

            CREDITS             = Emulator.getConfig().getInt("hotel.auto.credits.amount");
        }
        else
        {
            this.disposed = true;
        }
    }

    @Override
    public void run()
    {
        super.run();

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
