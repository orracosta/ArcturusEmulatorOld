package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.Connection;
import java.sql.Statement;

public class PointsCommand extends Command
{
    public PointsCommand()
    {
        super("cmd_points", Emulator.getTexts().getValue("commands.keys.cmd_points").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        try
        {
            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
            HabboInfo hb = HabboManager.getOfflineHabboInfo(params[1]);

            if(habbo != null)
            {
                try
                {
                    int type = Emulator.getConfig().getInt("seasonal.primary.type");

                    String tipo = params[2];
                    String alertstr = "evento";

                    int amount = 0;

                    switch (tipo)
                    {
                        case "promo":
                            amount = Integer.valueOf(Emulator.getConfig().getInt("cmd.points.amount.promo"));
                            alertstr = "promoção";
                            break;
                        default:
                            amount = Integer.valueOf(Emulator.getConfig().getInt("cmd.points.amount.event"));
                            alertstr = "evento";
                        break;
                    }

                    if (amount != 0)
                    {
                        habbo.givePoints(type, amount);

                        if(habbo.getHabboInfo().getCurrentRoom() != null)
                            habbo.whisper(Emulator.getTexts().getValue("commands.generic.cmd_points.received").replace("%amount%", amount + "").replace("%type%", alertstr), RoomChatMessageBubbles.ALERT);
                        else
                            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_points.received").replace("%amount%", amount + "").replace("%type%", alertstr)));

                        habbo.getClient().sendResponse(new UserPointsComposer(habbo.getHabboInfo().getCurrencyAmount(type), amount, type));

                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_points.send").replace("%amount%", amount + "").replace("%user%", params[1]).replace("%type%", alertstr), RoomChatMessageBubbles.ALERT);

                        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                        {
                            try (Statement statement = connection.createStatement())
                            {
                                if(tipo.equals("promo")) {
                                    statement.execute("UPDATE users SET promo_pts = promo_pts + 1 WHERE id = "+habbo.getHabboInfo().getId()+";");
                                }
                                else
                                {
                                    statement.execute("UPDATE users SET s_points = s_points + 1, g_points = g_points + 1, m_points = m_points + 1 WHERE id = "+habbo.getHabboInfo().getId()+";");
                                }
                            }
                        }

                    } else
                    {
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), RoomChatMessageBubbles.ALERT);
                    }
                }
                catch (NumberFormatException e)
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), RoomChatMessageBubbles.ALERT);
                }
            }
            else
            {
                if(hb != null)
                {
                    String tipo = params[2];

                    try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                    {
                        try (Statement statement = connection.createStatement())
                        {
                            if(tipo.equals("promo")) {
                                statement.execute("UPDATE users SET promo_pts = promo_pts + 1 WHERE id = "+hb.getId()+";");
                            }
                            else
                            {
                                statement.execute("UPDATE users SET s_points = s_points + 1, g_points = g_points + 1, m_points = m_points + 1 WHERE id = "+hb.getId()+";");
                            }
                        }
                    }
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_points.send_offline").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
                }
                else
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_points.user_not_found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
                }
            }
        }
        catch (NumberFormatException e)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
