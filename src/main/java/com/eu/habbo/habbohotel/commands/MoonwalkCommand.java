package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;

public class MoonwalkCommand extends Command
{
    public MoonwalkCommand()
    {
        super("cmd_moonwalk", Emulator.getTexts().getValue("commands.keys.cmd_moonwalk").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboStats().hasActiveClub())
        {
            if (gameClient.getHabbo().getRoomUnit().getEffectId() != 136)
            {
                gameClient.getHabbo().getRoomUnit().setEffectId(136);
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(gameClient.getHabbo().getRoomUnit()).compose());
            }
            else
            {
                gameClient.getHabbo().getRoomUnit().setEffectId(0);
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(gameClient.getHabbo().getRoomUnit()).compose());
            }

            return true;
        }

        return false;
    }
}
