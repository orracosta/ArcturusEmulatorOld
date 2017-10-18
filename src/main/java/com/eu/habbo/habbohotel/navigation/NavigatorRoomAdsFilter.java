package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.List;

public class NavigatorRoomAdsFilter extends NavigatorFilter
{
    public final static String name = "roomads_view";

    public NavigatorRoomAdsFilter()
    {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo)
    {
        boolean showInvisible = habbo.hasPermission("acc_enter_anyroom") || habbo.hasPermission("acc_anyroomowner");
        List<SearchResultList> resultList = new ArrayList<SearchResultList>();
        resultList.add(new SearchResultList(0, "categories", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("categories", ListMode.LIST), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("official-root", DisplayMode.VISIBLE), Emulator.getGameEnvironment().getRoomManager().getRoomsPromoted(), false, showInvisible));
        return resultList;
    }
}