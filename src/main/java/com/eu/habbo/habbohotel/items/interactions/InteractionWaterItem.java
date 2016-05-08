package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWaterItem extends InteractionDefault
{
    public InteractionWaterItem(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionWaterItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPlace()
    {
        this.update();
    }

    @Override
    public void onPickUp()
    {
        this.setExtradata("0");
        this.needsUpdate(true);
    }

    @Override
    public void onMove(Tile oldLocation, Tile newLocation)
    {
        this.update();
    }

    public void update()
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return;

        Rectangle rectangle = PathFinder.getSquare(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        for(int x = rectangle.x; x < rectangle.getWidth() + rectangle.x; x++)
        {
            for(int y = rectangle.y; y < rectangle.getHeight() + rectangle.y; y++)
            {
                THashSet<HabboItem> items = room.getItemsAt(x, y);

                for(HabboItem item : items)
                {
                    if(item instanceof InteractionWater)
                    {
                        this.setExtradata("1");
                        room.updateItem(this);
                        return;
                    }
                }
            }
        }

        this.setExtradata("0");
        room.updateItem(this);
    }
}
