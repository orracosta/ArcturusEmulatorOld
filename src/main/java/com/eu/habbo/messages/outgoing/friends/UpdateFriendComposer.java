package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UpdateFriendComposer extends MessageComposer
{
    private MessengerBuddy buddy;

    private Habbo habbo;

    public UpdateFriendComposer(MessengerBuddy buddy)
    {
        this.buddy = buddy;
    }

    /**
     * Updates the staff chat so the look matches the user. Does not do any other stuff.
     * @param habbo
     */
    public UpdateFriendComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {

        this.response.init(Outgoing.UpdateFriendComposer);
        if(buddy != null)
        {
            this.response.appendInt(0);
            this.response.appendInt(1);
            this.response.appendInt(0);
            this.response.appendInt(this.buddy.getId());
            this.response.appendString(this.buddy.getUsername());
            this.response.appendInt(this.buddy.getGender().equals(HabboGender.M) ? 0 : 1);
            this.response.appendBoolean(this.buddy.getOnline() == 1);
            this.response.appendBoolean(this.buddy.inRoom()); //In room
            this.response.appendString(this.buddy.getLook());
            this.response.appendInt(0);
            this.response.appendString(this.buddy.getMotto());
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendShort(this.buddy.getRelation());
        }
        else
        {
            this.response.appendInt(0);
            this.response.appendInt(1);
            this.response.appendInt(0);
            this.response.appendInt(-1);
            this.response.appendString("Staff Chat");
            this.response.appendInt(0);
            this.response.appendBoolean(true);
            this.response.appendBoolean(false); //In room
            this.response.appendString(this.habbo.getHabboInfo().getLook());
            this.response.appendInt(0);
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendShort(0);
        }
        return this.response;
    }
}
