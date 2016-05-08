package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

/**
 * Created on 4-11-2014 17:31.
 */
public class ModToolRoomAlertEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            this.packet.readInt();
            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new GenericAlertComposer(this.packet.readString()).compose());
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.roomalert").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
