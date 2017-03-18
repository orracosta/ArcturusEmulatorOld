package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GivePoints extends RCONMessage<GivePoints.JSONGivePoints>
{
    /**
     * Sends points of any type (including pixels) to an user.
     * Updates the database if the user is not online.
     */
    public GivePoints()
    {
        super(JSONGivePoints.class);
    }

    @Override
    public void handle(Gson gson, JSONGivePoints object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.username);

        if (habbo != null)
        {
            habbo.givePoints(object.type, object.points);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_currency INNER JOIN users ON users_currency.user_id = users.id SET users_currency.amount = users_currency.amount + ? WHERE users.username = ? AND users_currency.type = ?"))
            {
                statement.setInt(1, object.points);
                statement.setString(2, object.username);
                statement.setInt(3, object.type);
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

    public class JSONGivePoints
    {
        private String username;
        private int points;
        private int type;
    }
}
