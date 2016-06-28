package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWater extends InteractionDefault
{
    public InteractionWater(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionWater(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onMove(Room room, Tile oldLocation, Tile newLocation)
    {
        this.refreshWaters(room);
    }

    @Override
    public void onPickUp()
    {
        this.refreshWaters(null);
    }

    @Override
    public void onPlace()
    {
        this.refreshWaters(null);
    }

    private void refreshWaters(Room room)
    {
        if(room == null)
        {
            room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        }

        for(HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWaterItem.class))
        {
            ((InteractionWaterItem)item).update();
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        AbstractPet pet = room.getPet(roomUnit);

        if (pet != null)
        {
            if(pet instanceof Pet)
            {
                pet.getRoomUnit().getStatus().put("dip", "0");
            }
        }
    }
}
