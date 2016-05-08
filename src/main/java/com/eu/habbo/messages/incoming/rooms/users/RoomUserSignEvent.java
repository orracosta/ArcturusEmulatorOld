package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 13-9-2014 17:54.
 */
public class RoomUserSignEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int signId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        this.client.getHabbo().getRoomUnit().getStatus().put("sign", signId + "");
        this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(this.client.getHabbo());
    }
}
