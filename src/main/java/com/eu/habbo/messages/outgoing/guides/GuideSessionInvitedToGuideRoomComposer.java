package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-10-2015 21:44.
 */
public class GuideSessionInvitedToGuideRoomComposer extends MessageComposer
{
    private final Room room;

    public GuideSessionInvitedToGuideRoomComposer(Room room)
    {
        this.room = room;
    }

    //Helper invites noob
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuideSessionInvitedToGuideRoomComposer);
        this.response.appendInt32(this.room != null ? this.room.getId() : 0);
        this.response.appendString(this.room != null ? this.room.getName() : "");
        return this.response;
    }
}
