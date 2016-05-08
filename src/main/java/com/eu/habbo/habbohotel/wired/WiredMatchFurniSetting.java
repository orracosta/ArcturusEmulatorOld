package com.eu.habbo.habbohotel.wired;

/**
 * Created on 20-12-2014 12:13.
 */
public class WiredMatchFurniSetting
{
    public final int itemId;
    public final String state;
    public final int rotation;
    public final int x;
    public final int y;
    public final double z;

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y, double z)
    {
        this.itemId = itemId;
        this.state = state.isEmpty() ? "\t" : state;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString()
    {
        return itemId + "-" + state + "-" + rotation + "-" + x + "-" + y + "-" + z;
    }

}
