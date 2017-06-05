package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;

public class PointsCommand extends Command
{
    public PointsCommand()
    {
        super("cmd_points", Emulator.getTexts().getValue("commands.keys.cmd_points").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length >= 3)
        {
            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);

            if(habbo != null)
            {
                try
                {
                    int type = Emulator.getConfig().getInt("seasonal.primary.type");

                    if(params.length == 4)
                    {
                        try
                        {
                            type = Integer.valueOf(params[3]);
                        }
                        catch (Exception e)
                        {
                            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_type").replace("%types%", Emulator.getConfig().getValue("seasonal.types").replace(";", ", ")), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                            return true;
                        }
                    }

                    int amount = Integer.valueOf(params[2]);
                    if (amount != 0)
                    {
                        habbo.givePoints(type, amount);

                        if(habbo.getHabboInfo().getCurrentRoom() != null)
                            habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_points.received").replace("%amount%", amount + "").replace("%type%", Emulator.getTexts().getValue("seasonal.name." + type)), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                        else
                            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_points.received").replace("%amount%", amount + "").replace("%type%", Emulator.getTexts().getValue("seasonal.name." + type))));

                        habbo.getClient().sendResponse(new UserPointsComposer(habbo.getHabboInfo().getCurrencyAmount(type), amount, type));

                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_points.send").replace("%amount%", amount + "").replace("%user%", params[1]).replace("%type%", Emulator.getTexts().getValue("seasonal.name." + type)), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

                    } else
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    }
                }
                catch (NumberFormatException e)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_points.user_offline").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_points.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
        return true;
    }
}
