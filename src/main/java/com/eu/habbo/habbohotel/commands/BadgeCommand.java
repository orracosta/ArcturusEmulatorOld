package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import gnu.trove.map.hash.THashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BadgeCommand extends Command
{
    public BadgeCommand()
    {
        super("cmd_badge", Emulator.getTexts().getValue("commands.keys.cmd_badge").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 1)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_badge.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        if(params.length == 2)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_badge.forgot_badge").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if(params.length == 3)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

            if(habbo != null)
            {
                if(habbo.getInventory().getBadgesComponent().hasBadge(params[2]))
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", params[1]).replace("%badge%", params[2]), RoomChatMessageBubbles.ALERT);
                    return true;
                }

                HabboBadge badge = new HabboBadge(0, params[2], 0, habbo);

                badge.run();

                habbo.getInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));

                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", params[1]).replace("%badge%", params[2]), RoomChatMessageBubbles.ALERT);

                THashMap<String, String> keys = new THashMap<String, String>();
                keys.put("display", "BUBBLE");
                keys.put("image", "${image.library.url}album1584/" + badge.getCode() + ".gif");
                keys.put("message", Emulator.getTexts().getValue("commands.generic.cmd_badge.received"));
                habbo.getClient().sendResponse(new BubbleAlertComposer(BubbleAlertKeys.RECEIVED_BADGE.key, keys));
                return true;
            }
            else
            {
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                {
                    boolean found = false;

                    try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE users.username = ? AND badge_code = ? LIMIT 1"))
                    {
                        statement.setString(1, params[1]);
                        statement.setString(2, params[2]);
                        try (ResultSet set = statement.executeQuery())
                        {
                            found = set.next();
                        }
                    }

                    if(found)
                    {
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", params[1]).replace("%badge%", params[2]), RoomChatMessageBubbles.ALERT);
                        return true;
                    }
                    else
                    {
                        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE username = ? LIMIT 1), 0, ?)"))
                        {
                            statement.setString(1, params[1]);
                            statement.setString(2, params[2]);
                            statement.execute();
                        }

                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", params[1]).replace("%badge%", params[2]), RoomChatMessageBubbles.ALERT);
                        return true;
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }

        return true;
    }
}
