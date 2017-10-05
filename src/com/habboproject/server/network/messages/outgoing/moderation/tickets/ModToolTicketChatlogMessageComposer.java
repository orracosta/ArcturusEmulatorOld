package com.habboproject.server.network.messages.outgoing.moderation.tickets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class ModToolTicketChatlogMessageComposer extends MessageComposer {

    private final static String ROOM_ID = "roomId";
    private final static String ROOM_NAME = "roomName";

    private final int roomId;
    private final String roomName;

    private final List<RoomChatLogEntry> roomChatLogEntries;
    private final HelpTicket helpTicket;

    public ModToolTicketChatlogMessageComposer(HelpTicket helpTicket, final int roomId, final String roomName, final List<RoomChatLogEntry> chatLogs) {
        this.helpTicket = helpTicket;
        this.roomId = roomId;
        this.roomName = roomName;

        this.roomChatLogEntries = chatLogs;
    }

    @Override
    public short getId() {
        return Composers.ModeratorTicketChatlogMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.helpTicket.getId());
        msg.writeInt(this.helpTicket.getSubmitterId());
        msg.writeInt(this.helpTicket.getReportedId());
        msg.writeInt(this.helpTicket.getRoomId());

        msg.writeByte(1);
        msg.writeShort(2);

        msg.writeString(ROOM_ID);
        msg.writeByte(1);
        msg.writeInt(roomId);

        msg.writeString(ROOM_NAME);
        msg.writeByte(2);
        msg.writeString(roomName);

        msg.writeShort(roomChatLogEntries.size());

        for (RoomChatLogEntry entry : roomChatLogEntries) {
            entry.compose(msg);
        }
    }
}
