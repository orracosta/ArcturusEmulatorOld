package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.plugin.events.users.UserTalkEvent;

public class RoomUserTalkEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if(room == null)
            return;

        if(!this.client.getHabbo().getHabboStats().allowTalk())
            return;

        RoomChatMessage message = new RoomChatMessage(this);

        if (Emulator.getPluginManager().fireEvent(new UserTalkEvent(this.client.getHabbo(), message, RoomChatType.TALK)).isCancelled())
        {
            return;
        }

        room.talk(this.client.getHabbo(), message, RoomChatType.TALK);

        if (!message.isCommand)
        {
            if(RoomChatMessage.SAVE_ROOM_CHATS)
            {
                Emulator.getThreading().run(message);
            }
        }
    }
}
