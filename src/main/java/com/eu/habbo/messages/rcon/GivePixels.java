package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GivePixels extends RCONMessage<GivePixels.JSONGivePixels>
{
    /**
     * Sends pixels to an user.
     * Updates the database if the user is not online.
     */
    public GivePixels()
    {
        super(JSONGivePixels.class);
    }

    @Override
    public void handle(Gson gson, JSONGivePixels object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.username);

        if(habbo != null)
        {
            habbo.givePixels(object.pixels);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_currency INNER JOIN users ON users_currency.user_id = users.id SET users_currency.amount = users_currency.amount + ? WHERE users.username = ? AND users_currency.type = 0"))
            {
                statement.setInt(1, object.pixels);
                statement.setString(2, object.username);
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

    public class JSONGivePixels
    {
        private String username;
        private int pixels;
    }
}
