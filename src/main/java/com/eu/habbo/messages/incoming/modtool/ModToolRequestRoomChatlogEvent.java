package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolRoomChatlogComposer;

public class ModToolRequestRoomChatlogEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            this.packet.readInt();
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.packet.readInt());

            if(room != null)
                this.client.sendResponse(new ModToolRoomChatlogComposer(room, Emulator.getGameEnvironment().getModToolManager().getRoomChatlog(room.getId())));
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.chatlog").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
