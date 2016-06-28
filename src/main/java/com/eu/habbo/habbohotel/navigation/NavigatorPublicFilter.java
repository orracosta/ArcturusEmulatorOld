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
        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        int i = 0;
        resultLists.add(new SearchResultList(i, "official-root", "", SearchAction.NONE, SearchMode.THUMBNAILS, false, Emulator.getGameEnvironment().getRoomManager().getPublicRooms(), false));
        i++;
        for (NavigatorPublicCategory category : Emulator.getGameEnvironment().getNavigatorManager().publicCategories.values())
        {
            if (!category.rooms.isEmpty())
            {
                resultLists.add(new SearchResultList(i, "", category.name, SearchAction.NONE, category.image, false, category.rooms, true));
                i++;
            }
        }

        return resultLists;
    }
}