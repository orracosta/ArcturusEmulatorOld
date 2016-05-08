package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;

/**
 * Created on 4-10-2014 22:17.
 */
public class UnloadRoomCommand extends Command {

    public UnloadRoomCommand()
    {
        super.permission = "cmd_unload";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_unload").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getOwnerId() == gameClient.getHabbo().getHabboInfo().getId() || gameClient.getHabbo().getHabboInfo().getRank() > 4)
        {
            Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

            room.dispose();
            return true;
        }
        return false;
    }
}
