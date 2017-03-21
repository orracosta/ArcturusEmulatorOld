package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.CanCreateRoomComposer;

public class RequestCanCreateRoomEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int count = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()).size();
        int max = this.client.getHabbo().getHabboStats().hasActiveClub() ? Emulator.getConfig().getInt("hotel.max.rooms.vip") : Emulator.getConfig().getInt("hotel.max.rooms.user");
        this.client.sendResponse(new CanCreateRoomComposer(count, max));
    }
}
