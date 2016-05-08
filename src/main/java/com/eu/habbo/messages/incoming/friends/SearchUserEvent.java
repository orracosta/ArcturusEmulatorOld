package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.UserSearchResultComposer;

public class SearchUserEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String username = this.packet.readString().replace(" ", "");

        if(username.isEmpty())
            return;

        this.client.sendResponse(new UserSearchResultComposer(Messenger.searchUsers(username), this.client.getHabbo().getMessenger().getFriends(username), this.client.getHabbo()));
    }
}
