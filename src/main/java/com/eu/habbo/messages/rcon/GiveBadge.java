package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.google.gson.Gson;

import java.sql.*;

public class GiveBadge extends RCONMessage<GiveBadge.GiveBadgeJSON>
{
    /**
     * Sends a badge to an user.
     * Insers it if the user is not found.
     */
    public GiveBadge()
    {
        super(GiveBadgeJSON.class);
    }

    @Override
    public void handle(Gson gson, GiveBadgeJSON json)
    {
        if (json.user_id == -1)
        {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }
        
        if (json.badge.isEmpty())
        {
            this.status = RCONMessage.SYSTEM_ERROR;
            return;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        String username = json.user_id + "";
        if(habbo != null)
        {
            username = habbo.getHabboInfo().getUsername();

            if(habbo.getHabboInventory().getBadgesComponent().hasBadge(json.badge))
            {
                this.status = RCONMessage.STATUS_ERROR;
                this.message = Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", username).replace("%badge%", json.badge);
                return;
            }

            HabboBadge badge = new HabboBadge(0, json.badge, 0, habbo);

            badge.run();

            habbo.getHabboInventory().getBadgesComponent().addBadge(badge);
            habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));

            this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", json.badge);

            if (habbo.getHabboInfo().getCurrentRoom() != null)
            {
                habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_badge.received"), habbo, habbo, RoomChatMessageBubbles.ALERT)));
            }
            return;
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
            {
                boolean found = false;
                try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE users.id = ? AND badge_code = ? LIMIT 1"))
                {
                    statement.setInt(1, json.user_id);
                    statement.setString(2, json.badge);
                    try (ResultSet set = statement.executeQuery())
                    {
                        found = set.next();
                    }
                }

                if(found)
                {
                    this.status = RCONMessage.STATUS_ERROR;
                    this.message = Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", username).replace("%badge%", json.badge);
                }
                else
                {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE users.id = ? LIMIT 1), 0, ?)", Statement.RETURN_GENERATED_KEYS))
                    {
                        statement.setInt(1, json.user_id);
                        statement.setString(2, json.badge);
                        statement.execute();
                    }

                    this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", json.badge);
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
                this.status = RCONMessage.STATUS_ERROR;
                this.message = e.getMessage();
            }
        }
    }

    public class GiveBadgeJSON
    {
        public int user_id = -1;
        public String badge;
    }
}