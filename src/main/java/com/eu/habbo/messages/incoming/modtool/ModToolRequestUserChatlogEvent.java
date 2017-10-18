package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolManager;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolUserChatlogComposer;

public class ModToolRequestUserChatlogEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int userId = this.packet.readInt();
            String username = HabboManager.getOfflineHabboInfo(userId).getUsername();

            this.client.sendResponse(new ModToolUserChatlogComposer(Emulator.getGameEnvironment().getModToolManager().getUserRoomVisitsAndChatlogs(userId), userId, username));
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.chatlog").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
