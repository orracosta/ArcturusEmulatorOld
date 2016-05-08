package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

/**
 * Created on 9-7-2015 15:08.
 */
public class PixelScheduler implements Runnable
{
    private static boolean IGNORE_HOTEL_VIEW;
    private static boolean IGNORE_IDLED;

    private static int PIXELS;
    private static int INTERVAL;

    public boolean disposed = false;

    public PixelScheduler()
    {
        if(Emulator.getConfig().getBoolean("hotel.auto.pixels.enabled"))
        {
            IGNORE_HOTEL_VIEW   = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.hotelview");
            IGNORE_IDLED        = Emulator.getConfig().getBoolean("hotel.auto.pixels.ignore.idled");

            PIXELS              = Emulator.getConfig().getInt("hotel.auto.pixels.amount");
            INTERVAL            = Emulator.getConfig().getInt("hotel.auto.pixels.interval");

            Emulator.getThreading().run(this);
        }
    }

    @Override
    public void run()
    {
        if(this.disposed)
            return;

        Emulator.getThreading().run(this, INTERVAL * 1000);

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
