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

/**
 * Created on 18-10-2015 15:28.
 */
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
    public void onMove(Tile oldLocation, Tile newLocation)
    {
        this.refreshWaters();
    }

    @Override
    public void onPickUp()
    {
        this.refreshWaters();
    }

    @Override
    public void onPlace()
    {
        this.refreshWaters();
    }

    private void refreshWaters()
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return;

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
