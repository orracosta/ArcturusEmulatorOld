package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewNavigatorSearchResultsComposer extends MessageComposer
{
    private String searchCode;
    private String searchQuery;
    private final List<SearchResultList> resultList;

    public NewNavigatorSearchResultsComposer(String searchCode, String searchQuery, List<SearchResultList> resultList)
    {
        this.searchCode  = searchCode;
        this.searchQuery = searchQuery;
        this.resultList  = resultList;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorSearchResultsComposer);
        this.response.appendString(this.searchCode);
        this.response.appendString(this.searchQuery);

        Collections.sort(this.resultList, new Comparator<SearchResultList>()
        {
            @Override
            public int compare(SearchResultList o1, SearchResultList o2)
            {
                return o1.order - o2.order;
            }
        });

        this.response.appendInt(this.resultList.size()); //Count

        for (SearchResultList item : resultList)
        {
            item.serialize(this.response);
        }

        return this.response;
    }


}
