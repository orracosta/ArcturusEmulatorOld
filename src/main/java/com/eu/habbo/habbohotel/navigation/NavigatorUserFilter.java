package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigatorUserFilter extends NavigatorFilter
{
    public final static String name = "myworld_view";

    public NavigatorUserFilter()
    {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo)
    {
        int i = 0;
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        List<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(habbo);
        Collections.sort(rooms);
        resultLists.add(new SearchResultList(i, "my", "", SearchAction.NONE, SearchMode.LIST, false, rooms, true));
        i++;

        List<Room> favoriteRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsFavourite(habbo);

        if (!favoriteRooms.isEmpty())
        {
            Collections.sort(favoriteRooms);
            resultLists.add(new SearchResultList(1, "favorites", "", SearchAction.NONE, SearchMode.LIST, false, favoriteRooms, true));
            i++;
        }
        return resultLists;
    }
}