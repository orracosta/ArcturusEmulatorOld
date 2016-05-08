package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

public class ModToolAlertEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(habbo != null)
            {
                habbo.getClient().sendResponse(new GenericAlertComposer(message));
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.alert").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%message%", message));
        }
    }
}
