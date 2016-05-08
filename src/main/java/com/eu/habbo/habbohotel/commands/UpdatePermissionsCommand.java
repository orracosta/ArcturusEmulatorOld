package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 11-10-2014 15:18.
 */
public class UpdatePermissionsCommand extends Command
{
    public UpdatePermissionsCommand()
    {
        super.permission = "cmd_update_permissions";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_update_permissions").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Emulator.getGameEnvironment().getPermissionsManager().reload();

        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_update_permissions"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

        return true;
    }
}
