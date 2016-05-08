package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 21-10-2014 11:55.
 */
public class FreezeBotsCommand extends Command
{
    public FreezeBotsCommand()
    {
        super.permission = "cmd_freeze_bots";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_freeze_bots").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            if(gameClient.getHabbo().getHabboInfo().getId() == gameClient.getHabbo().getHabboInfo().getCurrentRoom().getOwnerId() || gameClient.getHabbo().hasPermission("acc_anyroomowner"))
            {
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().setAllowBotsWalk(!gameClient.getHabbo().getHabboInfo().getCurrentRoom().isAllowBotsWalk());
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(gameClient.getHabbo().getHabboInfo().getCurrentRoom().isAllowBotsWalk() ? Emulator.getTexts().getValue("commands.succes.cmd_freeze_bots.unfrozen") : Emulator.getTexts().getValue("commands.succes.cmd_freeze_bots.frozen"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("generic.cannot_do_that"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }

            return true;
        }

        return false;
    }
}
