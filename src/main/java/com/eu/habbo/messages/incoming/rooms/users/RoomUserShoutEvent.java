package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserShoutComposer;

/**
 * Created on 11-10-2014 16:07.
 */
public class RoomUserShoutEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        if(!this.client.getHabbo().getRoomUnit().canTalk())
            return;


        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), new RoomChatMessage(this), RoomChatType.SHOUT);
    }
}
