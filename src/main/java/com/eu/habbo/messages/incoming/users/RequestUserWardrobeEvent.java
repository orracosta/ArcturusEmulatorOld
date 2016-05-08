package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.UserWardrobeComposer;

/**
 * Created on 13-9-2014 19:35.
 */
public class RequestUserWardrobeEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new UserWardrobeComposer(this.client.getHabbo().getHabboInventory().getWardrobeComponent()));
    }
}
