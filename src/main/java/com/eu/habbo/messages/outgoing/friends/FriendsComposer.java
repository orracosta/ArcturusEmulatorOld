package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Map;

public class FriendsComposer extends MessageComposer
{
    private final Habbo habbo;

    public FriendsComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        try
        {
            this.response.init(Outgoing.FriendsComposer);

            //this.response.appendInt(300);
            //this.response.appendInt(300);
            //this.response.appendInt(3); //Club level
            this.response.appendInt(300);
            this.response.appendInt(300);
            this.response.appendInt(this.habbo.getMessenger().getFriends().size() + (this.habbo.hasPermission("acc_staff_chat") ? 1 : 0));

            for (Map.Entry<Integer, MessengerBuddy> row : this.habbo.getMessenger().getFriends().entrySet()) {
                this.response.appendInt(row.getKey());
                this.response.appendString(row.getValue().getUsername());
                this.response.appendInt(row.getValue().getGender().equals(HabboGender.M) ? 0 : 1);
                this.response.appendBoolean(row.getValue().getOnline() == 1);
                this.response.appendBoolean(row.getValue().inRoom()); //IN ROOM
                this.response.appendString(row.getValue().getLook());
                this.response.appendInt(0);
                this.response.appendString(row.getValue().getMotto());
                this.response.appendString("");
                this.response.appendString("");
                this.response.appendBoolean(false); //Offline messaging.
                this.response.appendBoolean(false);
                this.response.appendBoolean(false);
                this.response.appendShort(row.getValue().getRelation());
            }

            if(habbo.hasPermission("acc_staff_chat"))
            {
                this.response.appendInt(-1);
                this.response.appendString("Staff Chat");
                this.response.appendInt(this.habbo.getHabboInfo().getGender().equals(HabboGender.M) ? 0 : 1);
                this.response.appendBoolean(true);
                this.response.appendBoolean(false); //IN ROOM
                this.response.appendString("ADM");
                this.response.appendInt(0);
                this.response.appendString("");
                this.response.appendString("");
                this.response.appendString("");
                this.response.appendBoolean(true); //Offline messaging.
                this.response.appendBoolean(false);
                this.response.appendBoolean(false);
                this.response.appendShort(0);
            }
            return this.response;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}