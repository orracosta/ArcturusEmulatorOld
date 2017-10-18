package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;

public class EjectAllCommand extends Command
{
    public EjectAllCommand()
    {
        super("cmd_ejectall", Emulator.getTexts().getValue("commands.keys.cmd_ejectall").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            if(room.isOwner(gameClient.getHabbo()) || (room.hasGuild() && room.guildRightLevel(gameClient.getHabbo()) == 3))
            {
                room.ejectAll(gameClient.getHabbo());
            }
        }

        return true;
    }
}
