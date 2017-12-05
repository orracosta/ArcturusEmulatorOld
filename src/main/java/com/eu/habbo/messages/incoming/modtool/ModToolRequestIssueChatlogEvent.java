package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueChatlogComposer;

import java.util.ArrayList;

public class ModToolRequestIssueChatlogEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(this.packet.readInt());

            if(issue != null)
            {
                ArrayList<ModToolChatLog> chatlog;

                if (issue.type == ModToolTicketType.IM)
                {
                    chatlog = Emulator.getGameEnvironment().getModToolManager().getMessengerChatlog(issue.reportedId, issue.senderId);
                }
                else
                {
                    if (issue.roomId > 0)
                    {
                        chatlog = Emulator.getGameEnvironment().getModToolManager().getRoomChatlog(issue.roomId);
                    } else
                    {
                        chatlog = new ArrayList<ModToolChatLog>();
                        chatlog.addAll(Emulator.getGameEnvironment().getModToolManager().getUserChatlog(issue.reportedId));
                        chatlog.addAll(Emulator.getGameEnvironment().getModToolManager().getUserChatlog(issue.senderId));
                    }
                }

                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(issue.roomId);
                String roomName = "";

                if(room != null)
                {
                    roomName = room.getName();
                }
                this.client.sendResponse(new ModToolIssueChatlogComposer(issue, chatlog, roomName));
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.chatlog").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
