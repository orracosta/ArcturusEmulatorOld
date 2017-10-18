package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomWordFilterModifyEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();
        boolean add = this.packet.readBoolean();
        String word = this.packet.readString();

        if (word.length() > 25)
        {
            word = word.substring(0, 24);
        }

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
