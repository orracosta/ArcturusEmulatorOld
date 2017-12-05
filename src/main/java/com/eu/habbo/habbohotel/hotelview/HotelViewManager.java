package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;

public class HotelViewManager
{
    private final HallOfFame hallOfFame;
    private final NewsList newsList;

    public HotelViewManager()
    {
        long millis = System.currentTimeMillis();
        this.hallOfFame = new HallOfFame();
        this.newsList = new NewsList();

        Emulator.getLogging().logStart("Hotelview Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public HallOfFame getHallOfFame()
    {
        return hallOfFame;
    }

    public NewsList getNewsList()
    {
        return newsList;
    }

    public void dispose()
    {
        Emulator.getLogging().logShutdownLine("HotelView Manager -> Disposed!");
    }
}
