package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolUserRoomVisitsComposer;

public class ModToolRequestRoomVisitsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int userId = this.packet.readInt();

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(habbo != null)
                this.client.sendResponse(new ModToolUserRoomVisitsComposer(habbo, Emulator.getGameEnvironment().getModToolManager().requestUserRoomVisits(habbo)));
        }
    }
}
