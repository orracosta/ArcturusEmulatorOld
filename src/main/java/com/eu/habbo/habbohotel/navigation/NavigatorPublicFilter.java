package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.ArrayList;
import java.util.List;

public class NavigatorPublicFilter extends NavigatorFilter
{
    public final static String name = "official_view";

    public NavigatorPublicFilter()
    {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo)
    {
        boolean showInvisible = habbo.hasPermission("acc_enter_anyroom") || habbo.hasPermission("acc_anyroomowner");
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        int i = 0;
        resultLists.add(new SearchResultList(i, "official-root", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("official-root", ListMode.THUMBNAILS), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("official-root"), Emulator.getGameEnvironment().getRoomManager().getPublicRooms(), false, showInvisible));
        i++;
        for (NavigatorPublicCategory category : Emulator.getGameEnvironment().getNavigatorManager().publicCategories.values())
        {
            if (!category.rooms.isEmpty())
            {
                resultLists.add(new SearchResultList(i, "", category.name, SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(category.name, category.image), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(category.name), category.rooms, true, showInvisible));
                i++;
            }
        }

        return resultLists;
    }
}