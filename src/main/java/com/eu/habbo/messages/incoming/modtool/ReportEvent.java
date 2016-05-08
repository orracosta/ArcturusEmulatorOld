package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolReportReceivedAlertComposer;
import com.eu.habbo.threading.runnables.InsertModToolIssue;

public class ReportEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String message = this.packet.readString();
        int topic = this.packet.readInt();
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        int messageCount = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if(room == null)
            return;

        if(Emulator.getGameEnvironment().getModToolManager().hasPendingTickets(this.client.getHabbo().getHabboInfo().getId()))
        {
            this.client.sendResponse(new GenericAlertComposer("You've got still a pending ticket. Wait till the moderators are done reviewing your ticket."));
            return;
        }

        if(userId != -1)
        {
            Habbo reported = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if(reported != null)
            {
                ModToolIssue issue = new ModToolIssue(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getUsername(), reported.getHabboInfo().getId(), reported.getHabboInfo().getUsername(), roomId, message, ModToolTicketType.NORMAL);
                issue.category = topic;
                new InsertModToolIssue(issue).run();

                Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
                this.client.sendResponse(new ModToolReportReceivedAlertComposer(ModToolReportReceivedAlertComposer.REPORT_RECEIVED));

                for(int i = 0; i < messageCount; i++)
                {
                    //System.out.println(this.packet.readString());
                }
            }
        }
        else
        {
            ModToolIssue issue = new ModToolIssue(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getUsername(), room.getOwnerId(), room.getOwnerName(), roomId, message, ModToolTicketType.ROOM);
            issue.category = topic;
            new InsertModToolIssue(issue).run();

            this.client.sendResponse(new ModToolReportReceivedAlertComposer(ModToolReportReceivedAlertComposer.REPORT_RECEIVED));
            Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
            Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
        }
    }
}
