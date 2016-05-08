package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;

/**
 * Created on 17-11-2015 19:42.
 */
public class IgnoreRoomUserEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            String username = this.packet.readString();

            Habbo habbo = room.getHabbo(username);

            if(habbo != null)
            {
                if(habbo == this.client.getHabbo())
                    return;

                if(!habbo.getRoomUnit().isModMuted())
                {
                    this.client.getHabbo().getHabboStats().ignoredUsers.add(habbo.getHabboInfo().getId());
                    this.client.sendResponse(new RoomUserIgnoredComposer(habbo, RoomUserIgnoredComposer.IGNORED));
                }
            }
        }
    }
}
