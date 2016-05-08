package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 29-11-2014 11:45.
 */
public class RoomVoteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Emulator.getGameEnvironment().getRoomManager().voteForRoom(this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getCurrentRoom());
    }
}
