package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.procedure.TObjectProcedure;

public class RoomKickCommand extends Command
{
    public RoomKickCommand()
    {
        super("cmd_kickall", Emulator.getTexts().getValue("commands.keys.cmd_kickall").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        final Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if(room != null)
        {
            if(params.length > 1)
            {
                String message = "";
                for (int i = 1; i < params.length; i++)
                {
                    message += params[i] + " ";
                }
                room.sendComposer(new GenericAlertComposer(message  + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername()).compose());
            }

            for (Habbo habbo : room.getHabbos())
            {
                if (!(habbo.hasPermission("acc_unkickable") || habbo.hasPermission("acc_supporttool") || room.isOwner(habbo)))
                {
                    room.kickHabbo(habbo, true);
                }
            }
        }
        return true;
    }
}
