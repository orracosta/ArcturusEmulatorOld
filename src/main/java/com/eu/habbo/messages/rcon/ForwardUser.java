package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

public class ForwardUser extends RCONMessage<ForwardUser.ForwardUserJSON>
{
    public ForwardUser()
    {
        super(ForwardUserJSON.class);
    }

    @Override
    public void handle(Gson gson, ForwardUserJSON object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if(habbo != null)
        {
            Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(object.room_id);

            if(room != null)
            {
                Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, room);
            }
            else
            {
                this.status = RCONMessage.ROOM_NOT_FOUND;
            }
        }

        this.status = RCONMessage.HABBO_NOT_FOUND;
    }

    public class ForwardUserJSON
    {
        public int user_id;
        public int room_id;
    }
}
