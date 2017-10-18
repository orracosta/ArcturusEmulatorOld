package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.plugin.events.users.UserTalkEvent;

public class RoomUserWhisperEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        RoomChatMessage chatMessage = new RoomChatMessage(this);

        if(!this.client.getHabbo().getHabboStats().allowTalk() || chatMessage.getTargetHabbo() == null)
            return;

        if (Emulator.getPluginManager().fireEvent(new UserTalkEvent(this.client.getHabbo(), chatMessage, RoomChatType.WHISPER)).isCancelled())
        {
            return;
        }

        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), chatMessage, RoomChatType.WHISPER, true);

        if(RoomChatMessage.SAVE_ROOM_CHATS)
        {
            Emulator.getThreading().run(chatMessage);
        }
    }
}
