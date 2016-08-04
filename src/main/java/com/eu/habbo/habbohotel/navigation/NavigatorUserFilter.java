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
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        List<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(habbo);
        Collections.sort(rooms);
        resultLists.add(new SearchResultList(0, "my", "", SearchAction.NONE, SearchMode.LIST, false, rooms, true));
        return resultLists;
    }
}