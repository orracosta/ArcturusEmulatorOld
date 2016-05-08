package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-2-2015 15:31.
 */
public class MessengerInitComposer extends MessageComposer
{
    private Habbo habbo;

    public MessengerInitComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MessengerInitComposer);
        this.response.appendInt32(300);
        this.response.appendInt32(1337);
        this.response.appendInt32(500);
        this.response.appendInt32(1000);
        this.response.appendInt32(0); //Category count
        //this.response.appendInt32(this.habbo.getMessenger().getFriends().size() + (this.habbo.hasPermission("acc_staff_chat") ? 1 : 0));

        /*if(this.habbo.hasPermission("acc_staff_chat"))
        {
            this.response.appendInt32(0);
            this.response.appendString("Staff Chat");
        }

        for (Map.Entry<Integer, MessengerBuddy> map : this.habbo.getMessenger().getFriends().entrySet())
        {
            this.response.appendInt32(map.getKey());
            this.response.appendString(map.getValue().getUsername());
        }*/

        return this.response;
    }
}
