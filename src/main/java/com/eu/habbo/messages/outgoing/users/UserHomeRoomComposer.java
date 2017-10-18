package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserHomeRoomComposer extends MessageComposer
{

    private int homeRoom;
    private int newRoom;

    public UserHomeRoomComposer(int homeRoom, int newRoom)
    {
        this.homeRoom = homeRoom;
        this.newRoom = newRoom;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserHomeRoomComposer);

        this.response.appendInt(this.homeRoom);
        this.response.appendInt(this.newRoom);

        return this.response;
    }
}
