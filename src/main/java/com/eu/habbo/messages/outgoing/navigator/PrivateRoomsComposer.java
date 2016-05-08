package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

/**
 * Created on 31-8-2014 13:27.
 */
public class PrivateRoomsComposer extends MessageComposer{

    private List<Room> rooms;

    public PrivateRoomsComposer(List<Room> rooms)
    {
        this.rooms = rooms;
    }


    @Override
    public ServerMessage compose() {

        if(true)
            return null;

        try {
            this.response.init(Outgoing.PrivateRoomsComposer);

            this.response.appendInt32(2);
            this.response.appendString("");

            this.response.appendInt32(this.rooms.size());

            for (Room room : this.rooms) {
                room.serialize(this.response);
            }
            this.response.appendBoolean(true);

            this.response.appendInt32(0);
            this.response.appendString("A");
            this.response.appendString("B");
            this.response.appendInt32(1);
            this.response.appendString("C");
            this.response.appendString("D");
            this.response.appendInt32(1);
            this.response.appendInt32(1);
            this.response.appendInt32(1);
            this.response.appendString("E");
            return this.response;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
