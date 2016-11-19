package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavigatorHotelFilter extends NavigatorFilter
{
    public final static String name = "hotel_view";

    public NavigatorHotelFilter()
    {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo)
    {
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        int i = 0;
        resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, SearchMode.fromType(Emulator.getConfig().getInt("hotel.navigator.popular.listtype")), false, Emulator.getGameEnvironment().getRoomManager().getPopularRooms(Emulator.getConfig().getInt("hotel.navigator.popular.amount")), false, false));
        i++;

        for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().getPopularRoomsByCategory(Emulator.getConfig().getInt("hotel.navigator.search.maxresults")).entrySet())
        {
            if (!set.getValue().isEmpty())
            {
                RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
                resultLists.add(new SearchResultList(i, category.getCaption(), category.getCaption(), SearchAction.MORE, category.getDisplayMode(), false, set.getValue(), true, false));
                i++;
            }
        }

        return resultLists;
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo, NavigatorFilterField filterField, String value)
    {
        if (!filterField.databaseQuery.isEmpty())
        {
            List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
            int i = 0;
            resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, SearchMode.fromType(Emulator.getConfig().getInt("hotel.navigator.popular.listtype")), false, Emulator.getGameEnvironment().getRoomManager().getPopularRooms(10), false, false));
            i++;

            for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().findRooms(filterField, value).entrySet())
            {
                if (!set.getValue().isEmpty())
                {
                    RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
                    resultLists.add(new SearchResultList(i, category.getCaption(), category.getCaption(), SearchAction.MORE, category.getDisplayMode(), false, set.getValue(), true, false));
                    i++;
                }
            }

            return resultLists;
        }
        else
        {
            return getResult(habbo);
        }
    }
}