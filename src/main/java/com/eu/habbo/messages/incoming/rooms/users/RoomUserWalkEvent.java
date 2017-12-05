package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;

public class RoomUserWalkEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            int x = this.packet.readInt();
            int y = this.packet.readInt();

            Habbo habbo = this.client.getHabbo();
            RoomUnit roomUnit = this.client.getHabbo().getRoomUnit();

            if (roomUnit.isTeleporting)
                return;

            if (roomUnit.isKicked)
                return;

            if (roomUnit.getCacheable().get("control") != null)
            {
                habbo = (Habbo) roomUnit.getCacheable().get("control");

                if (habbo.getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom())
                {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            roomUnit = habbo.getRoomUnit();

            try
            {
                if (roomUnit != null && roomUnit.isInRoom() && roomUnit.canWalk())
                {
                    if (!roomUnit.cmdTeleport)
                    {
                        if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask() != null && habbo.getHabboInfo().getRiding().getTask().equals(PetTasks.JUMP))
                            return;

                        if (x == roomUnit.getX() && y == roomUnit.getY())
                            return;

                        RoomTile tile = habbo.getHabboInfo().getCurrentRoom().getLayout().getTile((short) x, (short) y);

                        if (tile == null)
                        {
                            return;
                        }

                        if (habbo.getRoomUnit().getStatus().containsKey("lay"))
                        {
                            if (habbo.getHabboInfo().getCurrentRoom().getLayout().getTilesInFront(habbo.getRoomUnit().getCurrentLocation(), habbo.getRoomUnit().getBodyRotation().getValue(), 2).contains(tile))
                                return;
                        }
                        if (tile.isWalkable() || habbo.getHabboInfo().getCurrentRoom().canSitOrLayAt(tile.x, tile.y))
                        {
                            roomUnit.setGoalLocation(tile);
                        }
                    }
                    else
                    {
                        RoomTile t = habbo.getHabboInfo().getCurrentRoom().getLayout().getTile((short) x, (short) y);
                        habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUnitOnRollerComposer(roomUnit, null, t, habbo.getHabboInfo().getCurrentRoom()).compose());

                        if (habbo.getHabboInfo().getRiding() != null)
                        {
                            habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), null, t, habbo.getHabboInfo().getCurrentRoom()).compose());
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }
}
