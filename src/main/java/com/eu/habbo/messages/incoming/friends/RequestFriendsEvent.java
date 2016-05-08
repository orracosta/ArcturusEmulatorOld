package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendsComposer;
import com.eu.habbo.messages.outgoing.friends.MessengerInitComposer;
import com.eu.habbo.messages.outgoing.friends.UpdateFriendComposer;
import com.eu.habbo.messages.outgoing.handshake.SessionRightsComposer;

import java.util.ArrayList;

/**
 * Created on 25-8-2014 11:46.
 */
public class RequestFriendsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {

        /*ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();

        messages.add(new FriendsComposer(this.client.getHabbo()).compose());
        messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());

        this.client.sendResponses(messages);*/

    }
}
