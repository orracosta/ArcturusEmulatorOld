package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomMutedComposer;

/**
 * Created on 21-8-2015 11:56.
 */
public class RoomMuteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            if(room.isOwner(this.client.getHabbo()))
            {
                room.setMuted(!room.isMuted());
                this.client.sendResponse(new RoomMutedComposer(room));
            }
        }
    }
}
