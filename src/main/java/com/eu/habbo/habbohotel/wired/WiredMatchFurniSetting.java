package com.eu.habbo.habbohotel.wired;

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
        this.state = state.replace("\t\t\t", " ");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString()
    {
        return this.toString(true);
    }

    public String toString(boolean includeState)
    {
        return itemId + "-" + (state.isEmpty() || !includeState ? " " : state) + "-" + rotation + "-" + x + "-" + y + "-" + z;
    }

}
