package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;

public class RoomUserTalkEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        if(!this.client.getHabbo().getRoomUnit().canTalk())
            return;

        RoomChatMessage message = new RoomChatMessage(this);
        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), message, RoomChatType.TALK);

        if (!message.isCommand)
        {
            if(Emulator.getConfig().getBoolean("save.room.chats", false))
            {
                Emulator.getThreading().run(message);
            }
        }
    }
}
