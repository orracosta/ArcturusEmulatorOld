package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class FreezeCommand extends Command
{
    public FreezeCommand()
    {
        super("cmd_freeze", Emulator.getTexts().getValue("commands.keys.cmd_freeze").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

            if(habbo == null)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_freeze.not_found").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else
            {
                if(habbo.getRoomUnit().canWalk())
                {
                    habbo.getRoomUnit().setCanWalk(false);
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_freeze.frozen"), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_freeze.user_frozen").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
                else
                {
                    habbo.getRoomUnit().setCanWalk(true);
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_freeze.unfrozen"), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_freeze.user_unfrozen").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_freeze.not_found").replace("%user%", ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
    }
}
