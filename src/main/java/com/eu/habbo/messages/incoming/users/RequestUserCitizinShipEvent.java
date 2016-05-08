package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.UserCitizinShipComposer;

/**
 * Created on 25-8-2014 12:56.
 */
public class RequestUserCitizinShipEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new UserCitizinShipComposer(this.packet.readString()));
    }
}
