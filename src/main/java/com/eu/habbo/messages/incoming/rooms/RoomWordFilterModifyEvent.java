package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 21-8-2015 12:35.
 */
public class RoomWordFilterModifyEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();
        boolean add = this.packet.readBoolean();
        String word = this.packet.readString();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if(room != null)
        {
            if (add)
            {
                room.addToWordFilter(word);
            }
            else
            {
                room.removeFromWordFilter(word);
            }
        }
    }
}
