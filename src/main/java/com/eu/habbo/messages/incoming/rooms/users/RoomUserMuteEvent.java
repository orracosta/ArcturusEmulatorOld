package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomUserMuteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(room.hasRights(this.client.getHabbo()) || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            int userId = this.packet.readInt();
            int roomId = this.packet.readInt();
            int minutes = this.packet.readInt();

            if(room.getId() == roomId)
            {
                Habbo habbo = room.getHabboByRoomUnitId(userId);

                if(habbo != null)
                {

                }
            }
        }
    }
}
