package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorSearchResultsComposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 19-6-2015 13:46.
 */
public class RequestNewNavigatorRoomsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String searchCode = this.packet.readString();
        String text = this.packet.readString();

        List<Room> rooms = new ArrayList<Room>();
        String key = "popular";

        if(searchCode.equalsIgnoreCase("official_view"))
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().getPublicRooms();
            key = "official-root";
        }
        else if(searchCode.equalsIgnoreCase("hotel_view"))
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms();
            key = "popular";
        }
        else if(searchCode.equalsIgnoreCase("roomads_view"))
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsPromoted();
            key = "top_promotions";
        }
        else if(searchCode.equalsIgnoreCase("myworld_view"))
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo());
            key = "my";
        }
        else
        {
        }

        if(text.contains(":"))
        {
            String filterType = text.split(":")[0];
            String filterData = "";
            int index = 0;
            for(String s : text.split(":"))
            {
                if(index == 0)
                {
                    ++index;
                    continue;
                }

                filterData+= s;
            }

            if(filterType.equalsIgnoreCase("owner"))
            {
                if(searchCode.equalsIgnoreCase("hotel_view") || searchCode.equalsIgnoreCase("myworld_view"))
                {
                    rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(filterData);
                }

                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByOwner(rooms, filterData);
            }
            else if(filterType.equalsIgnoreCase("tag"))
            {
                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByTag(rooms, filterData);
            }
            else if(filterType.equalsIgnoreCase("group"))
            {
                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByGroup(rooms, filterData);
            }
            else
            {
            }
        }
        else
        {
            rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByNameAndDescription(rooms, text);
        }

        Collections.sort(rooms);
        this.client.sendResponse(new NewNavigatorSearchResultsComposer(searchCode, text, key, rooms));
    }
}
