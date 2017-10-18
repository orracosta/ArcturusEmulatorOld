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
        boolean showInvisible = habbo.hasPermission("acc_enter_anyroom") || habbo.hasPermission("acc_anyroomowner");
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        int i = 0;
        resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("popular", ListMode.fromType(Emulator.getConfig().getInt("hotel.navigator.popular.listtype"))), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("popular"), Emulator.getGameEnvironment().getRoomManager().getPopularRooms(Emulator.getConfig().getInt("hotel.navigator.popular.amount")), false, showInvisible));
        i++;

        for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().getPopularRoomsByCategory(Emulator.getConfig().getInt("hotel.navigator.popular.category.maxresults")).entrySet())
        {
            if (!set.getValue().isEmpty())
            {
                RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
                resultLists.add(new SearchResultList(i, category.getCaption(), category.getCaption(), SearchAction.MORE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(category.getCaptionSave()), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(category.getCaptionSave()), set.getValue(), true, showInvisible));
                i++;
            }
        }

        return resultLists;
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo, NavigatorFilterField filterField, String value, int roomCategory)
    {
        boolean showInvisible = habbo.hasPermission("acc_enter_anyroom") || habbo.hasPermission("acc_anyroomowner");
        if (!filterField.databaseQuery.isEmpty())
        {
            List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
            int i = 0;

            for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().findRooms(filterField, value, roomCategory).entrySet())
            {
                if (!set.getValue().isEmpty())
                {
                    RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
                    resultLists.add(new SearchResultList(i, category.getCaptionSave(), category.getCaption(), SearchAction.MORE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(category.getCaptionSave()), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(category.getCaptionSave()), set.getValue(), true, showInvisible));
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