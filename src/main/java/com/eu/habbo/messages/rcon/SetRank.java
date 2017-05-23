package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SetRank extends RCONMessage<SetRank.JSONSetRank>
{
    /**
     * Sets the rank for an user.
     * Updates the database if the user is not online.
     */
    public SetRank()
    {
        super(JSONSetRank.class);
    }

    @Override
    public void handle(Gson gson, JSONSetRank object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if(Emulator.getGameEnvironment().getPermissionsManager().getPermissionsForRank(object.rank) == null)
        {
            this.status = RCONMessage.SYSTEM_ERROR;
            this.message = "invalid rank";
        }

        if(habbo != null)
        {
            habbo.getHabboInfo().setRank(object.rank);
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET rank = ? WHERE id = ? LIMIT 1"))
            {
                statement.setInt(1, object.rank);
                statement.setInt(2, object.user_id);
                statement.execute();
            }
            catch (Exception e)
            {
                this.status = RCONMessage.SYSTEM_ERROR;
            }
        }

        this.message = "offline";
    }

    public class JSONSetRank
    {
        public int user_id;
        public int rank;
    }
}
