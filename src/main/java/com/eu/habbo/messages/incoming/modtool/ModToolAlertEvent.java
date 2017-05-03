package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolAlertEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            Emulator.getGameEnvironment().getModToolManager().alert(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.packet.readInt()), this.client.getHabbo(), this.packet.readString());
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.kick").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
