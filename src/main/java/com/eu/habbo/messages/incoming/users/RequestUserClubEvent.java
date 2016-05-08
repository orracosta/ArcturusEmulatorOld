package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.UserClubComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;

/**
 * Created on 25-8-2014 11:58.
 */
public class RequestUserClubEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new UserClubComposer(this.client.getHabbo()));
        this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
    }
}
