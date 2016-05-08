package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;

public class MoonwalkCommand extends Command
{
    public MoonwalkCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_moonwalk").split(";");
        super.permission = "cmd_moonwalk";
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            gameClient.getHabbo().getRoomUnit().setEffectId(136);
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(gameClient.getHabbo().getRoomUnit()).compose());

            return true;
        }

        return false;
    }
}
