package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.PrivateRoomsComposer;
import com.eu.habbo.plugin.events.navigator.NavigatorSearchResultEvent;

import java.util.ArrayList;

public class SearchRoomsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String name = this.packet.readString();

        String prefix = "";
        String query = name;
        ArrayList<Room> rooms;

        if(name.startsWith("owner:"))
        {
            query = name.split("owner:")[1];
            prefix = "owner:";
            rooms = (ArrayList<Room>) Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(name);
        }
        else if(name.startsWith("tag:"))
        {
            query = name.split("tag:")[1];
            prefix = "tag:";
            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(name);
        }
        else if(name.startsWith("group:"))
        {
            query = name.split("group:")[1];
            prefix = "group:";
            rooms = Emulator.getGameEnvironment().getRoomManager().getGroupRoomsWithName(name);
        }
        else
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithName(name);
        }

        NavigatorSearchResultEvent event = new NavigatorSearchResultEvent(this.client.getHabbo(), prefix, query, rooms);
        if(!Emulator.getPluginManager().fireEvent(event).isCancelled())
        {
            this.client.sendResponse(new PrivateRoomsComposer(rooms));
        }
    }
}
