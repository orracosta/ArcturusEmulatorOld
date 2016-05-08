package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.CanCreateRoomComposer;

public class RequestCanCreateRoomEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new CanCreateRoomComposer(Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()).size()));
    }
}
