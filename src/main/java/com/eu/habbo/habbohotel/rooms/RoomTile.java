package com.eu.habbo.habbohotel.rooms;

public class RoomTile
{
    public final short x;
    public final short y;
    public final short z;
    public RoomTileState state = RoomTileState.OPEN;

    private double stackHeight = 0;
    private boolean allowStack = true;
    private boolean walkable = true;

    /**
     * Pathfinder
     */
    private RoomTile previous = null;
    private boolean diagonally;
    private short movementPanelty;
    private short gCosts;
    private short hCosts;

    /**
     *  @param x
     * @param y
     * @param z
     * @param state
     * @param walkable
     * @param allowStack
     */
    public RoomTile(short x, short y, short z, RoomTileState state, boolean walkable, boolean allowStack)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stackHeight = z;

        this.state = state;

        this.walkable = walkable;
        this.allowStack = allowStack;
        if (this.state == RoomTileState.BLOCKED)
        {
            this.allowStack = false;
            this.walkable = false;
        }
    }

    public RoomTile(RoomTile tile)
    {
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.z;
        this.stackHeight = tile.stackHeight;
        this.state = tile.state;
        this.walkable = tile.walkable;
        this.allowStack = tile.allowStack;
        this.diagonally = tile.diagonally;
        this.gCosts = tile.gCosts;
        this.hCosts = tile.hCosts;

        if (this.state == RoomTileState.BLOCKED)
        {
            this.allowStack = false;
            this.walkable = false;
        }
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
                ((RoomTile) o).y == this.y;
    }

    public RoomTile copy()
    {
        return new RoomTile(this);
    }

    public double distance(RoomTile roomTile)
    {
        double x = this.x - roomTile.x;
        double y = this.y - roomTile.y;
        return Math.sqrt(x * x + y * y);
    }

    public boolean isDiagonally()
    {
        return this.diagonally;
    }

    public void isDiagonally(boolean isDiagonally)
    {
        this.diagonally = isDiagonally;
    }

    public RoomTile getPrevious()
    {
        return this.previous;
    }

    public void setPrevious(RoomTile previous)
    {
        this.previous = previous;
    }

    public void setMovementPanelty(short movementPanelty)
    {
        this.movementPanelty = movementPanelty;
    }

    public int getfCosts()
    {
        return this.gCosts + this.hCosts;
    }

    public int getgCosts()
    {
        return this.gCosts;
    }

    private void setgCosts(short gCosts)
    {
        this.gCosts = (short)(gCosts + this.movementPanelty);
    }

    void setgCosts(RoomTile previousRoomTile, int basicCost)
    {
        setgCosts((short)(previousRoomTile.getgCosts() + basicCost));
    }

    public void setgCosts(RoomTile previousRoomTile)
    {
        setgCosts(previousRoomTile, this.diagonally ? RoomLayout.DIAGONALMOVEMENTCOST : RoomLayout.BASICMOVEMENTCOST);
    }

    public int calculategCosts(RoomTile previousRoomTile)
    {
        if (this.diagonally)
        {
            return previousRoomTile.getgCosts() + 14 + this.movementPanelty;
        }

        return previousRoomTile.getgCosts() + 10 + this.movementPanelty;
    }

    public int calculategCosts(RoomTile previousRoomTile, int movementCost)
    {
        return previousRoomTile.getgCosts() + movementCost + this.movementPanelty;
    }

    int gethCosts()
    {
        return this.hCosts;
    }

    void sethCosts(short hCosts)
    {
        this.hCosts = hCosts;
    }

    public void sethCosts(RoomTile parent)
    {
        this.hCosts = (short)((Math.abs(this.x - parent.x) + Math.abs(this.y - parent.y)) * (parent.diagonally ? RoomLayout.DIAGONALMOVEMENTCOST : RoomLayout.BASICMOVEMENTCOST));
    }

    public String toString()
    {
        return "RoomTile (" + this.x + ", " + this.y + ", " + this.z + "): h: " + this.hCosts + " g: " + this.gCosts + " f: " + this.getfCosts();
    }

    public boolean isWalkable()
    {
        return this.walkable && this.state != RoomTileState.BLOCKED;
    }

    public void setWalkable(boolean walkable)
    {
        this.walkable = walkable;
    }

    public boolean is(short x, short y)
    {
        return this.x == x && this.y == y;
    }
}