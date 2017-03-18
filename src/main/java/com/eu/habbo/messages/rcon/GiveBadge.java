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
        if (json.username.isEmpty() && json.userid == -1)
        {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }
        
        if (json.badge.isEmpty())
        {
            this.status = RCONMessage.SYSTEM_ERROR;
            return;
        }

        Habbo habbo = null;

        if (!json.username.isEmpty())
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.username);
        else
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.userid);

        if(habbo != null)
        {
            json.username = habbo.getHabboInfo().getUsername();

            if(habbo.getHabboInventory().getBadgesComponent().hasBadge(json.badge))
            {
                this.status = RCONMessage.STATUS_ERROR;
                this.message = Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", json.username).replace("%badge%", json.badge);
                return;
            }

            HabboBadge badge = new HabboBadge(0, json.badge, 0, habbo);

            badge.run();

            habbo.getHabboInventory().getBadgesComponent().addBadge(badge);
            habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));

            this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", json.username).replace("%badge%", json.badge);

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
                try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE " + (json.username.isEmpty() ? "users.id = ?" : "users.username = ?") + " AND badge_code = ? LIMIT 1"))
                {
                    if (!json.username.isEmpty())
                    {
                        statement.setString(1, json.username);
                    }
                    else
                    {
                        statement.setInt(1, json.userid);
                    }

                    statement.setString(2, json.badge);
                    try (ResultSet set = statement.executeQuery())
                    {
                        found = set.next();
                    }
                }

                if(found)
                {
                    this.status = RCONMessage.STATUS_ERROR;
                    this.message = Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", json.username).replace("%badge%", json.badge);
                }
                else
                {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE " + (json.username.isEmpty() ? "users.id = ?" : "users.username = ?") + " LIMIT 1), 0, ?)", Statement.RETURN_GENERATED_KEYS))
                    {
                        if (!json.username.isEmpty())
                        {
                            statement.setString(1, json.username);
                        }
                        else
                        {
                            statement.setInt(1, json.userid);
                        }
                        statement.setString(2, json.badge);
                        statement.execute();
                    }

                    this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", json.username).replace("%badge%", json.badge);
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public class GiveBadgeJSON
    {
        public int userid = -1;
        public String username = "";
        public String badge;
    }
}