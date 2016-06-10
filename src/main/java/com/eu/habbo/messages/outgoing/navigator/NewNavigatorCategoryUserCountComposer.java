package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class NewNavigatorCategoryUserCountComposer extends MessageComposer
{
    public final List<RoomCategory> roomCategories;

    public NewNavigatorCategoryUserCountComposer(List<RoomCategory> roomCategories)
    {
        this.roomCategories = roomCategories;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorCategoryUserCountComposer);
        this.response.appendInt32(this.roomCategories.size());

        for (RoomCategory category : this.roomCategories)
        {
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendInt32(200);
        }
        return this.response;
    }
}