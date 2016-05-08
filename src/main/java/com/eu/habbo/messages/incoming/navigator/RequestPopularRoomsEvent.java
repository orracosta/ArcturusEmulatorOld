package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.PrivateRoomsComposer;

/**
 * Created on 31-8-2014 13:15.
 */
public class RequestPopularRoomsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        //int categoryId = Integer.valueOf(this.packet.readString());

        this.client.sendResponse(new PrivateRoomsComposer(Emulator.getGameEnvironment().getRoomManager().getActiveRooms(-1)));
    }
}
