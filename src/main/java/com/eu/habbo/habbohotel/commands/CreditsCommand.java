package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

public class CreditsCommand extends Command
{
    public CreditsCommand()
    {
        super.permission = "cmd_credits";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_credits").split(";");
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
                    int credits = Integer.parseInt(params[2]);
                    if (credits != 0)
                    {
                        habbo.getHabboInfo().addCredits(Integer.parseInt(params[2]));
                        habbo.getClient().sendResponse(new UserCreditsComposer(habbo));
                        if(habbo.getHabboInfo().getCurrentRoom() != null)
                            habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_credits.received").replace("%amount%", Integer.parseInt(params[2]) + ""), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                        else
                            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_credits.received").replace("%amount%", Integer.parseInt(params[2]) + "")));

                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_credits.send").replace("%amount%", Integer.parseInt(params[2]) + "").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

                    } else
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_credits.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    }
                }
                catch (NumberFormatException e)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_credits.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_credits.user_offline").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_credits.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
        return true;
    }
}
