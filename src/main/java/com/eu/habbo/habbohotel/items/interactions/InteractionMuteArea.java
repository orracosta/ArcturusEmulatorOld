package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import gnu.trove.map.hash.THashMap;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMuteArea extends InteractionCustomValues
{
    public static THashMap<String, String> defaultValues = new THashMap<String, String>()
    {
        {put("tilesLeft",  "0");}
        {put("tilesRight", "0");}
        {put("tilesFront", "0");}
        {put("tilesBack",  "0");}
    };

    public InteractionMuteArea(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, defaultValues);
    }

    public InteractionMuteArea(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, defaultValues);
    }

    public boolean inSquare(RoomTile location)
    {
        try
        {
            return new Rectangle(
                            this.getX() - Integer.valueOf(this.values.get("tilesBack")),
                            this.getY() + Integer.valueOf(this.values.get("tilesLeft")) - (Integer.valueOf(this.values.get("tilesLeft"))  + Integer.valueOf(this.values.get("tilesRight"))),
                            Integer.valueOf(this.values.get("tilesLeft"))  + Integer.valueOf(this.values.get("tilesRight")) + 1,
                            Integer.valueOf(this.values.get("tilesFront")) + Integer.valueOf(this.values.get("tilesBack"))  + 1).contains(location.x, location.y);
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
