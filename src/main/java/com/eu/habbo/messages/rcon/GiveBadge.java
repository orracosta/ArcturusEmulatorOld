package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GiveBadge extends RCONMessage<GiveBadge.GiveBadgeJSON>
{
    public GiveBadge()
    {
        super(GiveBadgeJSON.class);
    }

    @Override
    public void handle(GiveBadgeJSON json)
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
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE " + (json.username.isEmpty() ? "users.id = ?" : "users.username = ?") + " AND badge_code = ? LIMIT 1");

                if (!json.username.isEmpty())
                {
                    statement.setString(1, json.username);
                }
                else
                {
                    statement.setInt(1, json.userid);
                }

                statement.setString(2, json.badge);
                ResultSet set = statement.executeQuery();

                boolean found = set.next();

                set.close();
                statement.close();
                statement.getConnection().close();

                if(found)
                {
                    this.status = RCONMessage.STATUS_ERROR;
                    this.message = Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", json.username).replace("%badge%", json.badge);
                    return;
                }
                else
                {
                    PreparedStatement s = Emulator.getDatabase().prepare("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE " + (json.username.isEmpty() ? "users.id = ?" : "users.username = ?") + " LIMIT 1), 0, ?)");
                    if (!json.username.isEmpty())
                    {
                        statement.setString(1, json.username);
                    }
                    else
                    {
                        statement.setInt(1, json.userid);
                    }
                    s.setString(2, json.badge);
                    s.execute();
                    s.close();
                    s.getConnection().close();

                    this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", json.username).replace("%badge%", json.badge);
                    return;
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