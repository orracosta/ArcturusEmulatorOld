package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.PrivateRoomsComposer;

/**
 * Created on 31-8-2014 14:18.
 */
public class RequestHighestScoreRoomsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new PrivateRoomsComposer(Emulator.getGameEnvironment().getRoomManager().getRoomsByScore()));
    }
}
