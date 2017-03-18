package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.inventory.InventoryBadgesComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TakeBadgeCommand extends Command
{
    public TakeBadgeCommand()
    {
        super("cmd_take_badge", Emulator.getTexts().getValue("commands.keys.cmd_take_badge").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (params.length == 2)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_take_badge.forgot_badge"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
        else if ( params.length == 1)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_take_badge.forgot_username"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if (params.length == 3)
        {
            String username = params[1];
            String badge = params[2];

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

            if (habbo != null)
            {
                HabboBadge b = habbo.getHabboInventory().getBadgesComponent().removeBadge(badge);

                if (b == null)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_take_badge.no_badge").replace("%username%", username).replace("%badge%", badge), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                habbo.getClient().sendResponse(new InventoryBadgesComposer(habbo));
            }

            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_take_badge"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE users_badges FROM users_badges INNER JOIN users ON users_badges.user_id = users.id WHERE users.username LIKE ? AND badge_code LIKE ?"))
            {
                statement.setString(1, username);
                statement.setString(2, badge);
                statement.execute();
            }
        }

        return true;
    }
}