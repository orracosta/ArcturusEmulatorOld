package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketState;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolReleaseTicketEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int count = this.packet.readInt();

            while (count != 0)
            {
                count--;

                int ticketId = this.packet.readInt();

                ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(ticketId);

                if (issue == null)
                    continue;

                if (issue.modId != this.client.getHabbo().getHabboInfo().getId())
                    continue;

                issue.modId = 0;
                issue.modName = "";
                issue.state = ModToolTicketState.OPEN;

                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ticket.release").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
