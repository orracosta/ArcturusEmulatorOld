package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

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

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null)
            {
                Emulator.getGameEnvironment().getModToolManager().createBan(habbo, this.client.getHabbo(), expireDate, message, "account");
            }
            else
            {
                Emulator.getGameEnvironment().getModToolManager().createBan(userId, "offline", this.client.getHabbo(), expireDate, message, "account");
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ban").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
