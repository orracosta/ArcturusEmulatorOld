package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.procedure.TObjectProcedure;

public class DisableEffects extends Command
{
    public DisableEffects(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && room.hasRights(gameClient.getHabbo()))
        {
            room.setAllowEffects(!room.isAllowEffects());

            String message = Emulator.getTexts().getValue(room.isAllowEffects() ? "essentials.cmd_disable_effects.effects_enabled" : "essentials.cmd_disable_effects.effects_disabled");
            gameClient.getHabbo().whisper(message);
            room.getUsersWithRights().forEachValue(new TObjectProcedure<String>()
            {
                @Override
                public boolean execute(String object)
                {
                    Habbo habbo = room.getHabbo(object);

                    if (habbo != null)
                    {
                        habbo.whisper(message);
                    }
                    return true;
                }
            });

            return true;
        }

        return false;
    }
}