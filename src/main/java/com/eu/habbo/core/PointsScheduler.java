package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

public class PointsScheduler extends Scheduler
{
    private static boolean IGNORE_HOTEL_VIEW;
    private static boolean IGNORE_IDLED;

    private static int POINTS;
    private static int INTERVAL;

    public PointsScheduler()
    {
        super(Emulator.getConfig().getInt("hotel.auto.points.interval"));

        if(Emulator.getConfig().getBoolean("hotel.auto.points.enabled"))
        {
            IGNORE_HOTEL_VIEW   = Emulator.getConfig().getBoolean("hotel.auto.points.ignore.hotelview");
            IGNORE_IDLED        = Emulator.getConfig().getBoolean("hotel.auto.points.ignore.idled");

            POINTS              = Emulator.getConfig().getInt("hotel.auto.points.amount");
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

        Habbo habbo = null;
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

                    habbo.givePoints(POINTS);
                }
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public static boolean isIgnoreHotelView()
    {
        return IGNORE_HOTEL_VIEW;
    }

    public static void setIgnoreHotelView(boolean ignoreHotelView)
    {
        IGNORE_HOTEL_VIEW = ignoreHotelView;
    }

    public static boolean isIgnoreIdled()
    {
        return IGNORE_IDLED;
    }

    public static void setIgnoreIdled(boolean ignoreIdled)
    {
        IGNORE_IDLED = ignoreIdled;
    }

    public static int getPOINTS()
    {
        return POINTS;
    }

    public static void setPOINTS(int POINTS)
    {
        PointsScheduler.POINTS = POINTS;
    }

    public static int getINTERVAL()
    {
        return INTERVAL;
    }

    public static void setINTERVAL(int INTERVAL)
    {
        PointsScheduler.INTERVAL = INTERVAL;
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
