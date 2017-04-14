package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;

public class ModToolRoomAlertEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            this.packet.readInt();
            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new ModToolIssueHandledComposer(this.packet.readString()).compose());
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.roomalert").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
