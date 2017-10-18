package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolManager;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolRequestUserInfoEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            ModToolManager.requestUserInfo(this.client, this.packet);
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.userinfo").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
