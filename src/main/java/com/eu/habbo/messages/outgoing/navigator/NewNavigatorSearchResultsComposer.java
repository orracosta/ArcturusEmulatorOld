package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class NewNavigatorSearchResultsComposer extends MessageComposer
{
    private String searchCode;
    private String searchQuery;
    private String parentName;
    private List<Room> rooms;

    public NewNavigatorSearchResultsComposer(String searchCode, String searchQuery, String parentName, List<Room> rooms)
    {
        this.searchCode = searchCode;
        this.searchQuery = searchQuery;
        this.parentName = parentName;
        this.rooms = rooms;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorSearchResultsComposer);
        this.response.appendString(this.searchCode);
        this.response.appendString(this.searchQuery);

        this.response.appendInt32(1); //Count something idk.
        this.response.appendString(this.parentName);
        this.response.appendString(this.searchQuery);
        this.response.appendInt32(0);
        this.response.appendBoolean(false);
        this.response.appendInt32(2);
        this.response.appendInt32(this.rooms.size());

        for(Room room : this.rooms)
        {
            room.serialize(this.response);
        }



        return this.response;
    }
}
