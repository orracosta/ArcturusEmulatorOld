package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GiveCredits extends RCONMessage<GiveCredits.JSONGiveCredits>
{
    /**
     * Gives credits an user.
     * Updates the database if the user is not online.
     */
    public GiveCredits()
    {
        super(JSONGiveCredits.class);
    }

    @Override
    public void handle(Gson gson, JSONGiveCredits object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if (habbo != null)
        {
            habbo.giveCredits(object.credits);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET credits = credits + ? WHERE id = ? LIMIT 1"))
            {
                statement.setInt(1, object.credits);
                statement.setInt(2, object.user_id);
                statement.execute();
            }
            catch (SQLException e)
            {
                this.status = RCONMessage.SYSTEM_ERROR;
				Emulator.getLogging().logSQLException(e);
            }

            this.message = "offline";
        }
    }

    public class JSONGiveCredits
    {
        private int user_id;
        private int credits;
    }
}
