package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;

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
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation)
    {
        this.recalculate(room);
    }

    @Override
    public void onPickUp(Room room)
    {
        this.recalculate(room);
    }

    @Override
    public void onPlace(Room room)
    {
        this.recalculate(room);
    }

    public void refreshWaters(Room room)
    {
        if(room == null)
        {
            room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        }

        int _1  = 0;
        int _2  = 0;
        int _3  = 0;
        int _4  = 0;
        int _5  = 0;
        int _6  = 0;
        int _7  = 0;
        int _8  = 0;
        int _9  = 0;
        int _10 = 0;
        int _11 = 0;
        int _12 = 0;

        for(HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWaterItem.class))
        {
            ((InteractionWaterItem) item).update();
        }

        if (room.waterTiles.containsKey(this.getX() - 1) && room.waterTiles.get(this.getX() - 1).contains(this.getY() - 1)) _1  = 1;
        if (room.waterTiles.containsKey(this.getX()    ) && room.waterTiles.get(this.getX()    ).contains(this.getY() - 1)) _2  = 1;
        if (room.waterTiles.containsKey(this.getX() + 1) && room.waterTiles.get(this.getX() + 1).contains(this.getY() - 1)) _3  = 1;
        if (room.waterTiles.containsKey(this.getX() + 2) && room.waterTiles.get(this.getX() + 2).contains(this.getY() - 1)) _4  = 1;
        if (room.waterTiles.containsKey(this.getX() - 1) && room.waterTiles.get(this.getX() - 1).contains(this.getY()    )) _5  = 1;
        if (room.waterTiles.containsKey(this.getX() + 2) && room.waterTiles.get(this.getX() + 2).contains(this.getY()    )) _6  = 1;
        if (room.waterTiles.containsKey(this.getX() - 1) && room.waterTiles.get(this.getX() - 1).contains(this.getY() + 1)) _7  = 1;
        if (room.waterTiles.containsKey(this.getX() + 2) && room.waterTiles.get(this.getX() + 2).contains(this.getY() + 1)) _8  = 1;
        if (room.waterTiles.containsKey(this.getX() - 1) && room.waterTiles.get(this.getX() - 1).contains(this.getY() + 2)) _9  = 1;
        if (room.waterTiles.containsKey(this.getX()    ) && room.waterTiles.get(this.getX()    ).contains(this.getY() + 2)) _10 = 1;
        if (room.waterTiles.containsKey(this.getX() + 1) && room.waterTiles.get(this.getX() + 1).contains(this.getY() + 2)) _11 = 1;
        if (room.waterTiles.containsKey(this.getX() + 2) && room.waterTiles.get(this.getX() + 2).contains(this.getY() + 2)) _12 = 1;

        if (_2  == 0 && !room.getLayout().tileWalkable(this.getX()              , (short) (this.getY() - 1))) _2  = 1;
        if (_3  == 0 && !room.getLayout().tileWalkable((short) (this.getX() + 1), (short) (this.getY() - 1))) _3  = 1;
        if (_5  == 0 && !room.getLayout().tileWalkable((short) (this.getX() - 1),          this.getY()     )) _5  = 1;
        if (_6  == 0 && !room.getLayout().tileWalkable((short) (this.getX() + 2),          this.getY()     )) _6  = 1;
        if (_7  == 0 && !room.getLayout().tileWalkable((short) (this.getX() - 1), (short) (this.getY() + 1))) _7  = 1;
        if (_8  == 0 && !room.getLayout().tileWalkable((short) (this.getX() + 2), (short) (this.getY() + 1))) _8  = 1;
        if (_10 == 0 && !room.getLayout().tileWalkable(this.getX()              , (short) (this.getY() + 2))) _10 = 1;
        if (_11 == 0 && !room.getLayout().tileWalkable((short) (this.getX() + 1), (short) (this.getY() + 2))) _11 = 1;

        int result = 0;
        result |= _1  << 11;
        result |= _2  << 10;
        result |= _3  << 9;
        result |= _4  << 8;
        result |= _5  << 7;
        result |= _6  << 6;
        result |= _7  << 5;
        result |= _8  << 4;
        result |= _9  << 3;
        result |= _10 << 2;
        result |= _11 << 1;
        result |= _12     ;

        this.setExtradata(result + "");
        this.needsUpdate(true);
        room.updateItem(this);
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

    private void recalculate(Room room)
    {
        THashMap<Integer, TIntArrayList> tiles = new THashMap<Integer, TIntArrayList>();

        for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWater.class))
        {
            for (int i = 0; i < item.getBaseItem().getLength(); i++)
            {
                for (int j = 0; j < item.getBaseItem().getWidth(); j++)
                {
                    if (!tiles.containsKey(item.getX() + i))
                    {
                        tiles.put(item.getX() + i, new TIntArrayList());
                    }

                    tiles.get(item.getX() + i).add(item.getY() + j);
                }
            }
        }

        room.waterTiles = tiles;

        for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWater.class))
        {
            ((InteractionWater)item).refreshWaters(room);
        }
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }
}
