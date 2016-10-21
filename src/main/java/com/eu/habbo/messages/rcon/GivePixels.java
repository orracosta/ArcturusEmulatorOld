package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GivePixels extends RCONMessage<GivePixels.JSONGivePixels>
{
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
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_currency INNER JOIN users ON users_currency.user_id = users.id SET users_currency.amount = users_currency.amount + ? WHERE users.username = ? AND users_currency.type = 0");

            try
            {
                statement.setInt(1, object.pixels);
                statement.setString(2, object.username);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                this.status = RCONMessage.SYSTEM_ERROR;
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
