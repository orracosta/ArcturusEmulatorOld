package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 26-9-2015 13:15.
 */
public class UpdatePluginsCommand extends Command
{
    public UpdatePluginsCommand()
    {
        super.permission = "cmd_update_plugins";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_update_plugins").split(";");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Emulator.getPluginManager().reload();

        gameClient.getHabbo().whisper("This is an unsafe command and could possibly lead to memory leaks.\rIt is recommended to restart the emulator in order to reload plugins.");
        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_update_plugins").replace("%count%", Emulator.getPluginManager().getPlugins().size() + ""), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        return true;
    }
}
