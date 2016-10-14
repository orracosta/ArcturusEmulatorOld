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
        List<SearchResultList> resultList = new ArrayList<SearchResultList>();
        resultList.add(new SearchResultList(0, "categories", "", SearchAction.NONE, SearchMode.LIST, false, Emulator.getGameEnvironment().getRoomManager().getRoomsPromoted(), false, false));
        return resultList;
    }
}