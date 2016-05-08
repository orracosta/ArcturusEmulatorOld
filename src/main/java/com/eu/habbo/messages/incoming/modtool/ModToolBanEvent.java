package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 22-11-2014 12:34.
 */
public class ModToolBanEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int userId = this.packet.readInt();
            String message = this.packet.readString();
            int expireDate = (this.packet.readInt() * 3600) + Emulator.getIntUnixTimestamp();

            Emulator.getGameEnvironment().getModToolManager().createBan(userId, this.client.getHabbo(), expireDate, message);
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ban").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
