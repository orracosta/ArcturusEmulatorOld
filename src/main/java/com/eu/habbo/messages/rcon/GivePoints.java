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
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if (habbo != null)
        {
            habbo.givePoints(object.type, object.points);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_currency (`user_id`, `type`, `amount`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = amount + ?"))
            {
                statement.setInt(1, object.user_id);
                statement.setInt(2, object.type);
                statement.setInt(3, object.points);
                statement.setInt(4, object.points);
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
        private int user_id;
        private int points;
        private int type;
    }
}
