package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 22-11-2014 16:21.
 */
public class GuildBuyRoomsComposer extends MessageComposer
{
    private final THashSet<Room> rooms;

    public GuildBuyRoomsComposer(THashSet<Room> rooms)
    {
        this.rooms = rooms;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildBuyRoomsComposer);
        this.response.appendInt32(10);
        this.response.appendInt32(this.rooms.size());

        for (Room room : this.rooms)
        {
            this.response.appendInt32(room.getId());
            this.response.appendString(room.getName());
            this.response.appendBoolean(false);
        }
        this.response.appendInt32(5);

        this.response.appendInt32(10);
        this.response.appendInt32(3);
        this.response.appendInt32(4);

        this.response.appendInt32(25);
        this.response.appendInt32(17);
        this.response.appendInt32(5);

        this.response.appendInt32(25);
        this.response.appendInt32(17);
        this.response.appendInt32(3);

        this.response.appendInt32(29);
        this.response.appendInt32(11);
        this.response.appendInt32(4);

        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        return this.response;
    }
}
