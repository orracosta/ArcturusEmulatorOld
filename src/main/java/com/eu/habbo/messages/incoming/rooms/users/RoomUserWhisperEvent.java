package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class RoomUserWhisperEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        RoomChatMessage chatMessage = new RoomChatMessage(this);

        if(!this.client.getHabbo().getRoomUnit().canTalk() && chatMessage.getTargetHabbo() != null)
            return;

        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), chatMessage, RoomChatType.WHISPER, true);
    }
}
