package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolRoomChatlogComposer;

public class ModToolRequestRoomUserChatlogEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int userId = this.packet.readInt();

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(habbo != null)
            {
                Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

                if(room != null)
                {
                    this.client.sendResponse(new ModToolRoomChatlogComposer(room, Emulator.getGameEnvironment().getModToolManager().getRoomChatlog(room.getId())));
                }
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.chatlog").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
