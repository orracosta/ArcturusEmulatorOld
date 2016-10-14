package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.util.pathfinding.AbstractNode;

public class RoomTile extends AbstractNode
{
    public final short x;
    public final short y;
    public final short z;
    public RoomTileState state = RoomTileState.OPEN;

    private double stackHeight = 0;
    private boolean allowStack = true;

    public RoomTile(short x, short y, short z, RoomTileState state)
    {
        super (x, y);
        this.x = x;
        this.y = y;
        this.z = z;
        this.stackHeight = z;

        this.state = state;
    }

    public double getStackHeight()
    {
        return this.stackHeight;
    }

    public void setStackHeight(double stackHeight)
    {
        if (stackHeight >= 0)
        {
            this.stackHeight = stackHeight;
            this.allowStack = true;
        }
        else
        {
            this.allowStack = false;
            this.stackHeight = this.z;
        }
    }

    public boolean allowStack()
    {
        return this.allowStack;
    }

    public void allowStack(boolean allowStack)
    {
        this.allowStack = allowStack;
    }

    public short relativeHeight()
    {
        if (this.state == RoomTileState.BLOCKED || !allowStack)
        {
            return Short.MAX_VALUE;
        }

        return (short) (this.stackHeight * 256.0);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof RoomTile       &&
                ((RoomTile) o).x == this.x &&
                ((RoomTile) o).y == this.y &&
                ((RoomTile) o).z == this.z;
    }

    public RoomTile copy()
    {
        return new RoomTile(this.x, this.y, this.z, this.state);
    }

    public double distance(RoomTile roomTile)
    {
        double x = this.x - roomTile.x;
        double y = this.y - roomTile.y;
        return Math.sqrt(x * x + y * y);
    }
}