package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Calendar;

public class GuardianVotingRequestedComposer extends MessageComposer
{
    private final GuardianTicket ticket;

    public GuardianVotingRequestedComposer(GuardianTicket ticket)
    {
        this.ticket = ticket;
    }

    @Override
    public ServerMessage compose()
    {
        TIntIntHashMap mappedUsers = new TIntIntHashMap();
        mappedUsers.put(this.ticket.getReported().getHabboInfo().getId(), 0);

        Calendar c = Calendar.getInstance();
        c.setTime(this.ticket.getDate());

        String fullMessage = c.get(Calendar.YEAR) + " ";
        fullMessage += c.get(Calendar.MONTH) + " ";
        fullMessage += c.get(Calendar.DAY_OF_MONTH) + " ";
        fullMessage += c.get(Calendar.MINUTE) + " ";
        fullMessage += c.get(Calendar.SECOND) + ";";

        fullMessage += "\r";

        for(ModToolChatLog chatLog : this.ticket.getChatLogs())
        {
            if(!mappedUsers.containsKey(chatLog.habboId))
            {
                mappedUsers.put(chatLog.habboId, mappedUsers.size());
            }

            fullMessage += "unused;" + mappedUsers.get(chatLog.habboId) + ";" + chatLog.message + "\r";
        }

        this.response.init(Outgoing.GuardianVotingRequestedComposer);
        this.response.appendInt(this.ticket.getTimeLeft());
        this.response.appendString(fullMessage);

        //2015 10 17 14 24 30
        return this.response;
    }
}
