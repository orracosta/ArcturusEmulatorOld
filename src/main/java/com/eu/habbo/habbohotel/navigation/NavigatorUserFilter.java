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
        resultLists.add(new SearchResultList(i, "my", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("my"), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("my"), rooms, true, true));
        i++;

        List<Room> favoriteRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsFavourite(habbo);

        if (!favoriteRooms.isEmpty())
        {
            Collections.sort(favoriteRooms);
            resultLists.add(new SearchResultList(1, "favorites", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("favorites"), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("favorites"), favoriteRooms, true, true));
            i++;
        }

        List<Room> frequentlyVisited = Emulator.getGameEnvironment().getRoomManager().getRoomsVisited(habbo, false, 10);
        if (!frequentlyVisited.isEmpty())
        {
            resultLists.add(new SearchResultList(1, "history_freq", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("history_freq"), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("history_freq"), frequentlyVisited, true, true));
            i++;
        }

        List<Room> groupRooms = Emulator.getGameEnvironment().getRoomManager().getGroupRooms(habbo, 25);
        if (!groupRooms.isEmpty())
        {
            resultLists.add(new SearchResultList(1, "my_groups", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("my_groups"), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("my_groups"), groupRooms, true, true));
            i++;
        }

        List<Room> rightRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithRights(habbo);
        if (!rightRooms.isEmpty())
        {
            resultLists.add(new SearchResultList(1, "with_rights", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("with_rights"), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("with_rights"), rightRooms, true, true));
            i++;
        }

        return resultLists;
    }
}