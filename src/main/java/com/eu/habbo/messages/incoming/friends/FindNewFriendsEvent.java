package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendFindingRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;

import java.util.List;

/**
 * Created on 2-8-2015 13:39.
 */
public class FindNewFriendsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        List<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms(Emulator.getConfig().getInt("hotel.friendcategory", 0));

        if(!rooms.isEmpty())
        {
            Room room = rooms.get(0);

            if (room != null)
            {
                this.client.sendResponse(new ForwardToRoomComposer(room.getId()));
                this.client.sendResponse(new FriendFindingRoomComposer(FriendFindingRoomComposer.ROOM_FOUND));
                return;
            }
        }

        this.client.sendResponse(new FriendFindingRoomComposer(FriendFindingRoomComposer.NO_ROOM_FOUND));
    }
}
