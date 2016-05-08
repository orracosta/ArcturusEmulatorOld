package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserClubComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;
import com.google.gson.Gson;

import java.sql.PreparedStatement;

/**
 * Created on 3-1-2016 13:21.
 */
public class SetRank extends RCONMessage<SetRank.JSONSetRank>
{
    public SetRank()
    {
        super(JSONSetRank.class);
    }

    @Override
    public String handle(JSONSetRank object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.username);

        if(Emulator.getGameEnvironment().getPermissionsManager().getPermissionsForRank(object.rank) == null)
        {
            return new Gson().toJson("INVALID_RANK", String.class);
        }

        if(habbo != null)
        {
            habbo.getHabboInfo().setRank(object.rank);
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
        else
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users SET rank = ? WHERE username = ? LIMIT 1");

            try
            {
                statement.setInt(1, object.rank);
                statement.setString(2, object.username);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (Exception e)
            {
                new Gson().toJson("FAILED", String.class);
            }
        }

        return new Gson().toJson("OK", String.class);
    }

    public class JSONSetRank
    {
        public String username;
        public int rank;
    }
}
