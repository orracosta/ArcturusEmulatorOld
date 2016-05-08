package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;
import java.util.Collections;

public class ModToolIssueChatlogComposer extends MessageComposer
{
    private ModToolIssue issue;
    private ArrayList<ModToolChatLog> chatlog;
    private String roomName;

    public ModToolIssueChatlogComposer(ModToolIssue issue, ArrayList<ModToolChatLog> chatlog, String roomName)
    {
        this.issue = issue;
        this.chatlog = chatlog;
        this.roomName = roomName;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueChatlogComposer);
        this.response.appendInt32(this.issue.id);
        this.response.appendInt32(this.issue.senderId);
        this.response.appendInt32(this.issue.reportedId);
        this.response.appendInt32(this.issue.roomId);

        Collections.sort(chatlog);

        if(chatlog.isEmpty())
            return null;

        //for(ModToolRoomVisit visit : chatlog)
        //{
            this.response.appendByte(1);
            this.response.appendShort(2);
            this.response.appendString("roomName");
            this.response.appendByte(2);
            this.response.appendString(this.roomName);
            this.response.appendString("roomId");
            this.response.appendByte(1);
            this.response.appendInt32(this.issue.roomId);

            this.response.appendShort(this.chatlog.size());
            for(ModToolChatLog chatLog : this.chatlog)
            {
                this.response.appendInt32(Emulator.getIntUnixTimestamp() - chatLog.timestamp);
                this.response.appendInt32(0);
                this.response.appendString(chatLog.username);
                this.response.appendString(chatLog.message);
                this.response.appendBoolean(false);
            }
        //}

        return this.response;
    }
}
