package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolCloseTicketEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int state = this.packet.readInt();
            int something = this.packet.readInt();
            int ticketId = this.packet.readInt();

            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(ticketId);

            if (issue == null || issue.modId != this.client.getHabbo().getHabboInfo().getId())
                return;

            Habbo sender = Emulator.getGameEnvironment().getHabboManager().getHabbo(issue.senderId);

            switch (state)
            {
                case 1:
                    Emulator.getGameEnvironment().getModToolManager().closeTicketAsUseless(issue, sender);
                    break;

                case 2:
                    Emulator.getGameEnvironment().getModToolManager().closeTicketAsAbusive(issue, sender);
                    break;

                case 3:
                    Emulator.getGameEnvironment().getModToolManager().closeTicketAsHandled(issue, sender);
                    break;
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ticket.close").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
