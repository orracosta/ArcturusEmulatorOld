package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

/**
 * Created on 4-11-2014 16:54.
 */
public class ModToolUserChatlogComposer extends MessageComposer
{
    private ArrayList<ModToolRoomVisit> set;
    private int userId;
    private String username;

    public ModToolUserChatlogComposer(ArrayList<ModToolRoomVisit> set, int userId, String username)
    {
        this.set = set;
        this.userId = userId;
        this.username = username;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolUserChatlogComposer);

        this.response.appendInt32(this.userId);
        this.response.appendString(this.username);
        this.response.appendInt32(this.set.size());

        for(ModToolRoomVisit visit : set)
        {
            this.response.appendByte(1);
            this.response.appendShort(2);
            this.response.appendString("roomName");
            this.response.appendByte(2);
            this.response.appendString(visit.roomName);
            this.response.appendString("roomId");
            this.response.appendByte(1);
            this.response.appendInt32(visit.roomId);

            this.response.appendShort(visit.chat.size());
            for(ModToolChatLog chatLog : visit.chat)
            {
                this.response.appendInt32(Emulator.getIntUnixTimestamp() - chatLog.timestamp);
                this.response.appendInt32(chatLog.habboId);
                this.response.appendString(chatLog.username);
                this.response.appendString(chatLog.message);
                this.response.appendBoolean(false);
            }
        }
        return this.response;
    }
}
