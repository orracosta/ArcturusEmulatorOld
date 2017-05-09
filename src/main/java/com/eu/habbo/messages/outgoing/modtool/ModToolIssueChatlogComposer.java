package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolChatRecordDataContext;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ModToolIssueChatlogComposer extends MessageComposer
{
    public static SimpleDateFormat format = new SimpleDateFormat("HH:mm");
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

        //ChatRecordData
        //for(ModToolRoomVisit visit : chatlog)
        //{
            this.response.appendByte(1); //Report Type

            this.response.appendShort(3); //Context Count

        ModToolChatRecordDataContext.ROOM_NAME.append(this.response);
        this.response.appendString(this.roomName); //Value

        ModToolChatRecordDataContext.ROOM_ID.append(this.response);
        this.response.appendInt32(this.issue.roomId); //Value

        ModToolChatRecordDataContext.GROUP_ID.append(this.response);
        this.response.appendInt32(12); //Value

            this.response.appendShort(this.chatlog.size());
            for(ModToolChatLog chatLog : this.chatlog)
            {
                this.response.appendString(format.format(chatLog.timestamp * 1000l));
                this.response.appendInt32(chatLog.habboId);
                this.response.appendString(chatLog.username);
                this.response.appendString(chatLog.message);
                this.response.appendBoolean(false);
            }
        //}

        return this.response;
    }
}
