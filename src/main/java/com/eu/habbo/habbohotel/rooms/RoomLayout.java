package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class RoomLayout
{
    public static double MAXIMUM_STEP_HEIGHT = 1.1;
    public static boolean ALLOW_FALLING = true;
    protected static final int BASICMOVEMENTCOST = 10;
    protected static final int DIAGONALMOVEMENTCOST = 14;

    public  boolean CANMOVEDIAGONALY = true;
    private String name;
    private short doorX;
    private short doorY;
    private short doorZ;
    private int doorDirection;
    private String heightmap;
    private int mapSize;
    private int mapSizeX;
    private int mapSizeY;
    private RoomTile[][] roomTiles;
    private RoomTile doorTile;
    private Room room;

    public RoomLayout(ResultSet set, Room room) throws SQLException
    {
        this.room = room;
        try
        {
            this.name = set.getString("name");
            this.doorX = set.getShort("door_x");
            this.doorY = set.getShort("door_y");

            this.doorDirection = set.getInt("door_dir");
            this.heightmap = set.getString("heightmap");

            this.parse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void parse()
    {
        String[] modelTemp = this.heightmap.replace("\n", "").split(Character.toString('\r'));

        this.mapSize = 0;
        this.mapSizeX = modelTemp[0].length();
        this.mapSizeY = modelTemp.length;
        this.roomTiles = new RoomTile[this.mapSizeX][this.mapSizeY];

        for (short y = 0; y < this.mapSizeY; y++)
        {
            if(modelTemp[y].isEmpty() || modelTemp[y].equalsIgnoreCase("\r"))
            {
                continue;
            }

            for (short x = 0; x < this.mapSizeX; x++)
            {
                if(modelTemp[y].length() != this.mapSizeX)
                {
                    break;
                }

                String square = modelTemp[y].substring(x, x + 1).trim().toLowerCase();
                RoomTileState state = RoomTileState.OPEN;
                short height = 0;
                if (square.equalsIgnoreCase("x"))
                {
                    state = RoomTileState.BLOCKED;
                }
                else
                {
                    if (square.isEmpty()) {
                        height = 0;
                    }
                    else if (Emulator.isNumeric(square))
                    {
                        height = Short.parseShort(square);
                    }
                    else
                    {
                        height = (short) (10 + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(square.toUpperCase()));
                    }
                }
                this.mapSize += 1;

                this.roomTiles[x][y] = new RoomTile(x, y, height, state, true, true);
            }
        }

        this.doorTile = this.roomTiles[this.doorX][this.doorY];

        if (this.doorTile != null)
        {
            this.doorTile.allowStack(false);
            RoomTile doorFrontTile = this.getTileInFront(this.doorTile, this.doorDirection);

            if (doorFrontTile != null && this.tileExists(doorFrontTile.x, doorFrontTile.y))
            {
                if (this.roomTiles[doorFrontTile.x][doorFrontTile.y].state != RoomTileState.BLOCKED)
                {
                    if (this.doorZ != this.roomTiles[doorFrontTile.x][doorFrontTile.y].z || this.roomTiles[this.doorX][this.doorY].state != this.roomTiles[doorFrontTile.x][doorFrontTile.y].state)
                    {
                        this.doorZ = this.roomTiles[doorFrontTile.x][doorFrontTile.y].z;
                        this.roomTiles[this.doorX][this.doorY].state = RoomTileState.OPEN;
                    }
                }
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public short getDoorX()
    {
        return this.doorX;
    }

    public void setDoorX(short doorX)
    {
        this.doorX = doorX;
    }

    public short getDoorY()
    {
        return this.doorY;
    }

    public void setDoorY(short doorY)
    {
        this.doorY = doorY;
    }

    public int getDoorZ()
    {
        return this.doorZ;
    }

    public RoomTile getDoorTile()
    {
        return this.doorTile;
    }

    public int getDoorDirection()
    {
        return this.doorDirection;
    }

    public void setDoorDirection(int doorDirection)
    {
        this.doorDirection = doorDirection;
    }

    public void setHeightmap(String heightMap)
    {
        this.heightmap = heightMap;
    }

    public String getHeightmap()
    {
        return this.heightmap;
    }

    public int getMapSize()
    {
        return this.mapSize;
    }

    public int getMapSizeX()
    {
        return this.mapSizeX;
    }

    public int getMapSizeY()
    {
        return this.mapSizeY;
    }

    public short getHeightAtSquare(int x, int y)
    {
        if(x < 0 ||
           y < 0 ||
           x >= this.getMapSizeX() ||
           y >= this.getMapSizeY())
            return 0;

        return this.roomTiles[x][y].z;
    }

    public double getStackHeightAtSquare(int x, int y)
    {
        if(x < 0 ||
                y < 0 ||
                x >= this.getMapSizeX() ||
                y >= this.getMapSizeY())
            return 0;

        return this.roomTiles[x][y].getStackHeight();
    }

    public double getRelativeHeightAtSquare(int x, int y)
    {
        if(x < 0 ||
                y < 0 ||
                x >= this.getMapSizeX() ||
                y >= this.getMapSizeY())
            return 0;

        return this.roomTiles[x][y].relativeHeight();
    }

    public RoomTile getTile(short x, short y)
    {
        if (tileExists(x, y))
        {
            return this.roomTiles[x][y];
        }

        return null;
    }

    public boolean tileExists(short x, short y)
    {
        return !(x < 0 || y < 0 || x >= this.getMapSizeX() || y >= this.getMapSizeY());
    }

    public boolean tileWalkable(short x, short y)
    {
        return this.tileExists(x, y) && this.roomTiles[x][y].state == RoomTileState.OPEN && this.roomTiles[x][y].isWalkable();
    }

    public RoomTileState getStateAt(short x, short y)
    {
        return this.roomTiles[x][y].state;
    }
    public String getRelativeMap()
    {
        return this.heightmap.replace("\r\n", "\r").substring(0, this.heightmap.replace("\r\n", "\r").length());
    }

    public final Deque<RoomTile> findPath(RoomTile oldTile, RoomTile newTile)
    {
        LinkedList<RoomTile> openList = new LinkedList();
        try
        {
            if (this.room == null || !this.room.isLoaded() || oldTile == null || newTile == null || oldTile.equals(newTile) || (!newTile.isWalkable() && !this.room.canSitOrLayAt(newTile.x, newTile.y)))
                return openList;

            List<RoomTile> closedList = new LinkedList();

            openList.add(oldTile.copy());

            long startMillis = System.currentTimeMillis();
            while (true)
            {
                if (System.currentTimeMillis() - startMillis > 25)
                {
                    return new LinkedList<>();
                }

                RoomTile current = lowestFInOpen(openList);
                closedList.add(current);
                openList.remove(current);

                if ((current.x == newTile.x) && (current.y == newTile.y))
                {
                    return calcPath(findTile(openList, (short)oldTile.x, (short)oldTile.y), current);
                }

                List<RoomTile> adjacentNodes = getAdjacent(openList, current, newTile.x, newTile.y);

                for (RoomTile currentAdj : adjacentNodes)
                {
                    if (!currentAdj.isWalkable() && !(currentAdj.equals(newTile) && room.canSitOrLayAt(currentAdj.x, currentAdj.y))){ closedList.add(currentAdj); openList.remove(currentAdj); continue;}
                    //if (!room.getLayout().tileWalkable((short) currentAdj.x, (short) currentAdj.y)) continue;

                    double height = (room.getLayout().getStackHeightAtSquare(currentAdj.x, currentAdj.y) - room.getLayout().getStackHeightAtSquare(current.x, current.y));

                    if ((!ALLOW_FALLING && height < -MAXIMUM_STEP_HEIGHT) || (height > MAXIMUM_STEP_HEIGHT && !room.canLayAt(currentAdj.x, currentAdj.y)))
                        continue;

                    if (!this.room.isAllowWalkthrough() && room.hasHabbosAt(currentAdj.x, currentAdj.y)) continue;

                    //if (room.hasPetsAt(currentAdj.x, currentAdj.y)) continue;

                    if (!openList.contains(currentAdj) || (currentAdj.x == newTile.x && currentAdj.y == newTile.y && (room.canSitOrLayAt(newTile.x, newTile.y) && !room.hasHabbosAt(newTile.x, newTile.y))))
                    {
                        currentAdj.setPrevious(current);
                        currentAdj.sethCosts(findTile(openList, (short)newTile.x, (short)newTile.y));
                        currentAdj.setgCosts(current);
                        openList.add(currentAdj);
                    }
                    else if (currentAdj.getgCosts() > currentAdj.calculategCosts(current))
                    {
                        currentAdj.setPrevious(current);
                        currentAdj.setgCosts(current);
                    }
                }
                if (openList.isEmpty())
                {
                    return new LinkedList();
                }
            }
        }
        catch(Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
        return new LinkedList<>();
    }

    private RoomTile findTile(List<RoomTile> tiles, short x, short y)
    {
        for (RoomTile tile : tiles)
        {
            if (x == tile.x && y == tile.y)
            {
                return tile;
            }
        }

        RoomTile tile = this.getTile(x, y);

        if (tile != null)
        {
            return tile.copy();
        }
        return null;
    }

    private Deque<RoomTile> calcPath(RoomTile start, RoomTile goal)
    {
        LinkedList<RoomTile> path = new LinkedList();
        if (start == null)
            return path;

        RoomTile curr = goal;
        while (curr != null)
        {
            path.addFirst(curr);
            curr = curr.getPrevious();
            if ((curr != null) && (start != null) && (curr.equals(start))) {
                return path;
            }
        }
        return path;
    }

    private RoomTile lowestFInOpen(List<RoomTile> openList)
    {
        if(openList == null)
            return null;

        RoomTile cheapest = openList.get(0);
        for (RoomTile anOpenList : openList)
        {
            if (anOpenList.getfCosts() < cheapest.getfCosts())
            {
                cheapest = anOpenList;
            }
        }
        return cheapest;
    }

    private List<RoomTile> getAdjacent(List<RoomTile> closedList, RoomTile node, int newX, int newY)
    {
        short x = node.x;
        short y = node.y;
        List<RoomTile> adj = new LinkedList<RoomTile>();
        boolean canSitOrLayAt = room.canSitOrLayAt(newX, newY);
        if (x > 0)
        {
            RoomTile temp = findTile(adj, (short) (x - 1), y);
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (x < this.mapSizeX)
        {
            RoomTile temp = findTile(closedList, (short) (x + 1), y);
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (y > 0)
        {
            RoomTile temp = findTile(closedList, x, (short) (y - 1));
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (y < this.mapSizeY)
        {
            RoomTile temp = findTile(closedList, x, (short) (y + 1));
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (CANMOVEDIAGONALY)
        {
            if ((x < this.mapSizeX) && (y < this.mapSizeY))
            {
                RoomTile temp = findTile(closedList, (short) (x + 1), (short) (y + 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y > 0))
            {
                RoomTile temp = findTile(closedList, (short) (x - 1), (short) (y - 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y < this.mapSizeY))
            {
                RoomTile temp = findTile(closedList, (short) (x - 1), (short) (y + 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x < this.mapSizeX) && (y > 0))
            {
                RoomTile temp = findTile(closedList, (short) (x + 1), (short) (y - 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && canSitOrLayAt)))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
        }
        return adj;
    }

    public void moveDiagonally(boolean value)
    {
        this.CANMOVEDIAGONALY = value;
    }

    public static boolean squareInSquare(Rectangle outerSquare, Rectangle innerSquare)
    {
        if(outerSquare.x > innerSquare.x)
            return false;

        if(outerSquare.y > innerSquare.y)
            return false;

        if(outerSquare.x + outerSquare.width < innerSquare.x + innerSquare.width)
            return false;

        if(outerSquare.y + outerSquare.height < innerSquare.y + innerSquare.height)
            return false;

        return true;
    }

    public static boolean pointInSquare(int x1, int y1, int x2, int y2, int pointX, int pointY)
    {
        return (pointX >= x1 && pointY >= y1) && (pointX <= x2 && pointY <= y2);
    }

    public static boolean tilesAdjecent(RoomTile one, RoomTile two)
    {
        return !(one == null || two == null) && !(Math.abs(one.x - two.x) > 1) && !(Math.abs(one.y - two.y) > 1);
    }

    public RoomTile getTileInFront(RoomTile tile, int rotation)
    {
        return this.getTileInFront(tile, rotation, 0);
    }

    public RoomTile getTileInFront(RoomTile tile, int rotation, int offset)
    {
        int offsetX = 0;
        int offsetY = 0;

        rotation = rotation % 8;
        switch (rotation)
        {
            case 0: offsetY--; break;
            case 1: offsetX++; offsetY--; break;
            case 2: offsetX++; break;
            case 3: offsetX++; offsetY++; break;
            case 4: offsetY++; break;
            case 5: offsetX--; offsetY++; break;
            case 6: offsetX--; break;
            case 7: offsetX--; offsetY--; break;
        }

        short x = tile.x;
        short y = tile.y;

        for (int i = 0; i <= offset; i++)
        {
            x += offsetX;
            y += offsetY;
        }

        return this.getTile(x, y);
    }

    public List<RoomTile> getTilesInFront(RoomTile tile, int rotation, int amount)
    {
        List<RoomTile> tiles = new ArrayList<>(amount);
        RoomTile previous = tile;
        for (int i = 0; i < amount; i++)
        {
            RoomTile t = this.getTileInFront(previous, rotation, 1);

            if (t != null)
            {
                previous = t;
                tiles.add(t);
            }
            else
            {
                break;
            }
        }

        return tiles;
    }

    public List<RoomTile> getTilesAround(RoomTile tile)
    {
        List<RoomTile> tiles = new ArrayList<RoomTile>(8);

        if (tile != null)
        {
            for (int i = 0; i < 8; i++)
            {
                RoomTile t = this.getTileInFront(tile, i);
                if (t != null)
                {
                    tiles.add(t);
                }
            }
        }

        return tiles;
    }

    public static Rectangle getRectangle(int x, int y, int width, int length, int rotation)
    {
        rotation = (rotation % 8);

        if(rotation == 2 || rotation == 6)
        {
            return new Rectangle(x, y, length, width);
        }

        return new Rectangle(x, y, width, length);
    }

    public THashSet<RoomTile> getTilesAt(RoomTile tile, int width, int length, int rotation)
    {
        THashSet<RoomTile> pointList = new THashSet<RoomTile>(width * length, 0.1f);

        if (tile != null)
        {
            if (rotation == 0 || rotation == 4)
            {
                for (short i = tile.x; i <= (tile.x + (width - 1)); i++)
                {
                    for (short j = tile.y; j <= (tile.y + (length - 1)); j++)
                    {
                        RoomTile t = this.getTile(i, j);

                        if (t != null)
                        {
                            pointList.add(t);
                        }
                    }
                }
            }
            else if (rotation == 2 || rotation == 6)
            {
                for (short i = tile.x; i <= (tile.x + (length - 1)); i++)
                {
                    for (short j = tile.y; j <= (tile.y + (width - 1)); j++)
                    {
                        RoomTile t = this.getTile(i, j);

                        if (t != null)
                        {
                            pointList.add(t);
                        }
                    }
                }
            }
        }

        return pointList;
    }

    public static boolean tilesAdjecent(RoomTile tile, RoomTile comparator, int width, int length, int rotation)
    {
        Rectangle rectangle = getRectangle(comparator.x, comparator.y, width, length, rotation);
        rectangle = new Rectangle(rectangle.x - 1, rectangle.y -1, rectangle.width + 2, rectangle.height + 2);

        return rectangle.contains(tile.x, tile.y);
    }
}
