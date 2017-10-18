package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;
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
        try
        {
            Emulator.getGameEnvironment().getHabboManager().setRank(object.user_id, object.rank);
        }
        catch (Exception e)
        {
            this.status = RCONMessage.SYSTEM_ERROR;
            this.message = "invalid rank";
            return;
        }

        this.message = "updated offline user";

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if(habbo != null)
        {
            this.message = "updated online user";
        }
    }

    public class JSONSetRank
    {
        public int user_id;
        public int rank;
    }
}
