package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class UserSearchResultComposer extends MessageComposer
{
    private final THashSet<MessengerBuddy> users;
    private final THashSet<MessengerBuddy> friends;
    private final Habbo habbo;

    public UserSearchResultComposer(THashSet<MessengerBuddy> users, THashSet<MessengerBuddy> friends, Habbo habbo)
    {
        this.users = users;
        this.friends = friends;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserSearchResultComposer);
        THashSet<MessengerBuddy> u = new THashSet<MessengerBuddy>();

        for(MessengerBuddy buddy : this.users)
        {
            if(!buddy.getUsername().equals(this.habbo.getHabboInfo().getUsername()) && !inFriendList(buddy))
            {
                u.add(buddy);
            }
        }

        this.response.appendInt(this.friends.size());
        for(MessengerBuddy buddy : this.friends)
        {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getLook());
            this.response.appendString("");
        }

        this.response.appendInt(u.size());
        for(MessengerBuddy buddy : u)
        {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getLook());
            this.response.appendString("");
        }

        return this.response;
    }

    private boolean inFriendList(MessengerBuddy buddy)
    {
        for(MessengerBuddy friend : this.friends)
        {
            if(friend.getUsername().equals(buddy.getUsername()))
                return true;
        }

        return false;
    }
}
