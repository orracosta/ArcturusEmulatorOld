package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;

/**
 * Created on 13-9-2014 14:03.
 */
public class RoomUserTalkEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        if(!this.client.getHabbo().getRoomUnit().canTalk())
            return;

        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), new RoomChatMessage(this), RoomChatType.TALK);

    }
}
