package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.PublicRoomsComposer;

/**
 * Created on 26-8-2014 18:45.
 */
public class RequestPublicRoomsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new PublicRoomsComposer());
    }
}
