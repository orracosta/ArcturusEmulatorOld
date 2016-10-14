package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
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
        resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, SearchMode.THUMBNAILS, false, Emulator.getGameEnvironment().getRoomManager().getPopularRooms(10), false, true));
        i++;

        for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().getPopularRoomsByCategory(15).entrySet())
        {
            if (!set.getValue().isEmpty())
            {
                resultLists.add(new SearchResultList(i, Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey()).getCaption(), Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey()).getCaption(), SearchAction.MORE, SearchMode.LIST, set.getValue().size() > 10, set.getValue(), true, true));
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
            resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, SearchMode.THUMBNAILS, false, Emulator.getGameEnvironment().getRoomManager().getPopularRooms(10), false, true));
            i++;

            for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().findRooms(filterField, value).entrySet())
            {
                if (!set.getValue().isEmpty())
                {
                    resultLists.add(new SearchResultList(i, Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey()).getCaption(), Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey()).getCaption(), SearchAction.MORE, SearchMode.LIST, set.getValue().size() > 10, set.getValue(), true, true));
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