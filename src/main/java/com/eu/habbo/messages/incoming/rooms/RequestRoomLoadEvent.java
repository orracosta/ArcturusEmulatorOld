package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;

public class RequestRoomLoadEvent extends MessageHandler
{

    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();
        String password = this.packet.readString();

        if(this.client.getHabbo().getHabboInfo().getLoadingRoom() == 0 && this.client.getHabbo().getHabboStats().roomEnterTimestamp + 1000 < System.currentTimeMillis())
        {
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            if (room != null)
            {
                Emulator.getGameEnvironment().getRoomManager().logExit(this.client.getHabbo());

                if (this.client.getHabbo().getRoomUnit() != null)
                {
                    room.sendComposer(new RoomUserRemoveComposer(this.client.getHabbo().getRoomUnit()).compose());
                }
                room.removeHabbo(this.client.getHabbo());
                this.client.getHabbo().getHabboInfo().setCurrentRoom(null);
            }
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), roomId, password);
        }
    }
}
