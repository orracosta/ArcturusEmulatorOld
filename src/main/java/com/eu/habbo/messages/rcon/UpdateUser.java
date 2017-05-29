package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUser extends RCONMessage<UpdateUser.JSON>
{
    public UpdateUser()
    {
        super(UpdateUser.JSON.class);
    }

    @Override
    public void handle(Gson gson, JSON json)
    {
        if (json.user_id > 0)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

            if (habbo != null)
            {
                habbo.getHabboStats().addAchievementScore(json.achievement_score);
            }
            else
            {
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_settings SET achievement_score = achievement_score + ? WHERE user_id = ? LIMIT 1"))
                {
                    statement.setInt(1, json.achievement_score);
                    statement.setInt(2, json.user_id);
                    statement.execute();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }
    }

    public class JSON
    {
        public int user_id;
        public int achievement_score = 0;
        //More could be added in the future.
    }
}