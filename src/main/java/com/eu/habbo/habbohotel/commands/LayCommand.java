package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.util.pathfinding.PathFinder;

public class LayCommand extends Command
{
    public LayCommand()
    {
        super (null, Emulator.getTexts().getValue("commands.keys.cmd_lay").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        gameClient.getHabbo().getRoomUnit().cmdLay = true;
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().updateHabbo(gameClient.getHabbo());
        gameClient.getHabbo().getRoomUnit().cmdSit = true;
        gameClient.getHabbo().getRoomUnit().setBodyRotation(RoomUserRotation.values()[gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() - gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() % 2]);

        RoomTile tile = gameClient.getHabbo().getRoomUnit().getCurrentLocation();
        for (int i = 0; i < 3; i++)
        {
            if (!gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(tile, gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue(), i).isWalkable())
            {
                return false;
            }
        }
        
        gameClient.getHabbo().getRoomUnit().getStatus().put("lay", 0.5 + "");
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserStatusComposer(gameClient.getHabbo().getRoomUnit()).compose());
        return true;
    }
}