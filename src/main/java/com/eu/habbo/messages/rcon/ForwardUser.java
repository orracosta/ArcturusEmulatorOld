package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

/**
 * Created on 5-9-2015 10:39.
 */
public class ForwardUser extends RCONMessage<ForwardUser.ForwardUserJSON>
{
    public ForwardUser()
    {
        super(ForwardUserJSON.class);
    }

    @Override
    public String handle(ForwardUserJSON object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if(habbo != null)
        {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(object.room_id);

            if(room != null)
            {
                Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, room);

                return new Gson().toJson("OK", String.class);
            }
            else
            {
                return new Gson().toJson("ROOM_ERROR", String.class);
            }
        }

        return new Gson().toJson("USER_ERROR", String.class);
    }

    public class ForwardUserJSON
    {
        public int user_id;
        public int room_id;
    }
}
