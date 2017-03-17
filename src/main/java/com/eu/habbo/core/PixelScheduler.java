package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

public class PixelScheduler extends Scheduler
{
    private static boolean IGNORE_HOTEL_VIEW;
    private static boolean IGNORE_IDLED;

    private static int PIXELS;
    private static int INTERVAL;

    public PixelScheduler()
    {
        super(Emulator.getConfig().getInt("hotel.auto.pixels.interval"));

        if(Emulator.getConfig().getBoolean("hotel.auto.pixels.enabled"))
        {
            IGNORE_HOTEL_VIEW   = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.hotelview");
            IGNORE_IDLED        = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.idled");

            PIXELS              = Emulator.getConfig().getInt("hotel.auto.pixels.amount");
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

                    habbo.givePixels(PIXELS);
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

    public static int getPIXELS()
    {
        return PIXELS;
    }

    public static void setPIXELS(int PIXELS)
    {
        PixelScheduler.PIXELS = PIXELS;
    }

    public static int getINTERVAL()
    {
        return INTERVAL;
    }

    public static void setINTERVAL(int INTERVAL)
    {
        PixelScheduler.INTERVAL = INTERVAL;
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
