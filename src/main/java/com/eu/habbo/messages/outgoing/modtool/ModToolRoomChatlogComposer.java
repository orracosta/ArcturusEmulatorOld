package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

/**
 * Created on 15-7-2015 13:51.
 */
public class ModToolRoomChatlogComposer extends MessageComposer
{
    private Room room;
    private ArrayList<ModToolChatLog> chatlog;

    public ModToolRoomChatlogComposer(Room room, ArrayList<ModToolChatLog> chatlog)
    {
        this.room = room;
        this.chatlog = chatlog;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolRoomChatlogComposer);
        this.response.appendByte(1);
        this.response.appendShort(2);
        this.response.appendString("roomName");
        this.response.appendByte(2);
        this.response.appendString(this.room.getName());
        this.response.appendString("roomId");
        this.response.appendByte(1);
        this.response.appendInt32(this.room.getId());

        this.response.appendShort(this.chatlog.size());
        for(ModToolChatLog line : this.chatlog)
        {
            this.response.appendInt32(line.timestamp);
            this.response.appendInt32(line.habboId);
            this.response.appendString(line.username);
            this.response.appendString(line.message);
            this.response.appendBoolean(false);
        }
        return this.response;
    }
}
