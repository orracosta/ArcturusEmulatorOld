package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueInfoComposer;

public class ModToolPickTicketEvent extends MessageHandler
{
    public static boolean send = false;
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            this.packet.readInt();
            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(this.packet.readInt());

            if(issue != null)
            {
                if(issue.state == ModToolTicketState.PICKED)
                {
                    this.client.sendResponse(new ModToolIssueInfoComposer(issue));
                    this.client.sendResponse(new GenericAlertComposer("Picking issue failedd: \rTicket already picked or does not exist!"));

                    return;
                }

                //this.client.sendResponse(new ModToolIssueInfoComposer(issue));
                Emulator.getGameEnvironment().getModToolManager().pickTicket(issue, this.client.getHabbo());
            }
            else
            {
                this.client.sendResponse(new GenericAlertComposer("Picking issue failed: \rTicket already picked or does not exist!"));
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ticket.pick").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
