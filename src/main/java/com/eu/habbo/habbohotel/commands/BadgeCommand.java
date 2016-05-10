package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;

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
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_badge.forgot_username"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
        if(params.length == 2)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_badge.forgot_badge").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if(params.length == 3)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

            if(habbo != null)
            {
                if(habbo.getHabboInventory().getBadgesComponent().hasBadge(params[2]))
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", params[1]).replace("%badge%", params[2]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                HabboBadge badge = new HabboBadge(0, params[2], 0, habbo);

                badge.run();

                habbo.getHabboInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));

                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", params[1]).replace("%badge%", params[2]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

                if (habbo.getHabboInfo().getCurrentRoom() != null)
                {
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_badge.received"), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                }
                return true;
            }
            else
            {
                try
                {
                    PreparedStatement statement = Emulator.getDatabase().prepare("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE users.username = ? AND badge_code = ? LIMIT 1");
                    statement.setString(1, params[1]);
                    statement.setString(2, params[2]);
                    ResultSet set = statement.executeQuery();

                    boolean found = set.next();

                    set.close();
                    statement.close();
                    statement.getConnection().close();

                    if(found)
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_badge.already_owns").replace("%user%", params[1]).replace("%badge%", params[2]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                        return true;
                    }
                    else
                    {
                        PreparedStatement s = Emulator.getDatabase().prepare("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE username = ? LIMIT 1), 0, ?)");
                        s.setString(1, params[1]);
                        s.setString(2, params[2]);
                        s.execute();
                        s.close();
                        s.getConnection().close();

                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", params[1]).replace("%badge%", params[2]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
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
