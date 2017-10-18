package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolRoomInfoComposer;

public class ModToolRequestRoomInfoEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            //int roomId = this.packet.readInt();

            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            //Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if (room != null)
            {
                this.client.sendResponse(new ModToolRoomInfoComposer(room));
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.roominfo").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
