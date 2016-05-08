package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;

public class PixelCommand extends Command
{
    public PixelCommand()
    {
        super.permission = "cmd_duckets";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_duckets").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 3)
        {
            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);

            if(habbo != null)
            {
                try
                {
                    if (Integer.parseInt(params[2]) != 0)
                    {
                        habbo.getHabboInfo().addPixels(Integer.parseInt(params[2]));
                        habbo.getClient().sendResponse(new UserCurrencyComposer(habbo));

                        if(habbo.getHabboInfo().getCurrentRoom() != null)
                            habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_duckets.received").replace("%amount%", Integer.valueOf(params[2]) + ""), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                        else
                            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_duckets.received").replace("%amount%", Integer.valueOf(params[2]) + "")));

                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_duckets.send").replace("%amount%", Integer.valueOf(params[2]) + "").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

                    } else
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_duckets.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    }
                }
                catch (NumberFormatException e)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_duckets.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_duckets.user_offline").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_duckets.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
        return true;
    }
}
