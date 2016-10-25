package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class DiagonalCommand extends Command
{
    public DiagonalCommand()
    {
        super("cmd_diagonal", Emulator.getTexts().getValue("commands.keys.cmd_diagonal").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo()))
            {
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally(gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally());

                if (!gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally())
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_diagonal.disabled"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_diagonal.enabled"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }

                return true;
            }
        }

        return false;
    }
}