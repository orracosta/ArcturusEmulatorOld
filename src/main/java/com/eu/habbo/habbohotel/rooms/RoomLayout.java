package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.util.pathfinding.PathFinder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class RoomLayout
{
    public static double MAXIMUM_STEP_HEIGHT = 1.1;
    public static boolean ALLOW_FALLING = true;
    protected static final int BASICMOVEMENTCOST = 5;
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
            this.heightmap = set.getString("heightmap").replace("\n", "");

            this.parse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void parse()
    {
        String[] modelTemp = this.heightmap.split(Character.toString('\r'));

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

        RoomTile doorFrontTile = PathFinder.getSquareInFront(this, this.doorX, this.doorY, this.doorDirection);

        if(doorFrontTile != null && this.tileExists(doorFrontTile.x, doorFrontTile.y))
        {
            if(this.roomTiles[doorFrontTile.x][doorFrontTile.y].state != RoomTileState.BLOCKED)
            {
                if (this.doorZ != this.roomTiles[doorFrontTile.x][doorFrontTile.y].z || this.roomTiles[this.doorX][this.doorY].state != this.roomTiles[doorFrontTile.x][doorFrontTile.y].state)
                {
                    this.doorZ = this.roomTiles[doorFrontTile.x][doorFrontTile.y].z;
                    this.roomTiles[this.doorX][this.doorY].state = RoomTileState.OPEN;
                    this.doorTile = this.roomTiles[this.doorX][this.doorY];
                    //this.roomTiles[this.doorX][this.doorY].z = this.doorZ;

//                    StringBuilder stringBuilder = new StringBuilder(this.heightmap);
//                    stringBuilder.setCharAt((this.doorY * (this.getMapSizeX() + 1)) + this.doorX, this.roomTiles[doorFrontTile.x][doorFrontTile.y].z >= 10 ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(this.roomTiles[doorFrontTile.x][doorFrontTile.y].z - 10) : ("" + (this.roomTiles[doorFrontTile.x][doorFrontTile.y]).z).charAt(0));
//                    this.heightmap = stringBuilder.toString();
//
//                    try
//                    {
//                        PreparedStatement statement;
//
//                        if (name.startsWith("custom_"))
//                        {
//                            statement = Emulator.getDatabase().prepare("UPDATE room_models_custom SET heightmap = ? WHERE name = ?");
//                        }
//                        else
//                        {
//                            statement = Emulator.getDatabase().prepare("UPDATE room_models SET heightmap = ? WHERE name = ?");
//                        }
//
//                        statement.setString(1, this.heightmap);
//                        statement.setString(2, this.name);
//                        statement.execute();
//                        statement.getConnection().close();
//                        statement.close();
//                    }
//                    catch (SQLException e)
//                    {
//                        Emulator.getLogging().logSQLException(e);
//                    }
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
        return this.tileExists(x, y) && this.roomTiles[x][y].state == RoomTileState.OPEN;
    }

    public RoomTileState getStateAt(short x, short y)
    {
        return this.roomTiles[x][y].state;
    }
    public String getRelativeMap()
    {
        return this.heightmap.replace("\r\n", "\r");
    }

    public final Deque<RoomTile> findPath(int oldX, int oldY, int newX, int newY)
    {
        LinkedList<RoomTile> openList = new LinkedList();
        try
        {
            if (room == null || !room.isLoaded())
                return openList;

            if ((oldX == newX) && (oldY == newY))
            {
                return openList;
            }

            if (oldX > this.mapSizeX ||
                    oldY > this.mapSizeY ||
                    newX > this.mapSizeX ||
                    newY > this.mapSizeY
                    )
                return openList;

            List<RoomTile> closedList = new LinkedList();

            openList.add(this.roomTiles[oldX][oldY].copy());

            boolean done = false;
            while (!done)
            {
                RoomTile current = lowestFInOpen(openList);
                closedList.add(current);
                openList.remove(current);

                List<RoomTile> adjacentNodes = getAdjacent(openList, current, newX, newY);

                if ((current.x == newX) && (current.y == newY))
                {
                    return calcPath(findTile(openList, (short)oldX, (short)oldY), current, room);
                }
                for (RoomTile currentAdj : adjacentNodes)
                {
                    if (!currentAdj.isWalkable()){ closedList.add(currentAdj); openList.remove(currentAdj); continue;}
                    //if (!room.getLayout().tileWalkable((short) currentAdj.x, (short) currentAdj.y)) continue;

                    double height = (room.getLayout().getStackHeightAtSquare(currentAdj.x, currentAdj.y) - room.getLayout().getStackHeightAtSquare(current.x, current.y));

                    if ((!ALLOW_FALLING && height < -MAXIMUM_STEP_HEIGHT) || height > MAXIMUM_STEP_HEIGHT)
                        continue;

                    if (!room.isAllowWalkthrough() && room.hasHabbosAt(currentAdj.x, currentAdj.y)) continue;

                    if (room.hasPetsAt(currentAdj.x, currentAdj.y)) continue;

                    if (!openList.contains(currentAdj) || (currentAdj.x == newX && currentAdj.y == newY && (room.canSitOrLayAt(newX, newY) && !room.hasHabbosAt(newX, newY))))
                    {
                        currentAdj.setPrevious(current);
                        currentAdj.sethCosts(findTile(openList, (short)newX, (short)newY));
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
        return null;
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

    private Deque<RoomTile> calcPath(RoomTile start, RoomTile goal, Room room)
    {
        LinkedList<RoomTile> path = new LinkedList();

        RoomTile curr = goal;
        boolean done = false;
        while (!done) {
            if (curr != null)
            {
                path.addFirst(curr);
                curr = curr.getPrevious();
                if ((curr != null) && (start != null) && (curr.equals(start))) {
                    done = true;
                }
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
        if (x > 0)
        {
            RoomTile temp = findTile(adj, (short) (x - 1), y);
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (x < this.mapSizeX)
        {
            RoomTile temp = findTile(closedList, (short) (x + 1), y);
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (y > 0)
        {
            RoomTile temp = findTile(closedList, x, (short) (y - 1));
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
            {
                temp.isDiagonally(false);
                adj.add(temp);
            }
        }
        if (y < this.mapSizeY)
        {
            RoomTile temp = findTile(closedList, x, (short) (y + 1));
            if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
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
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y > 0))
            {
                RoomTile temp = findTile(closedList, (short) (x - 1), (short) (y - 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y < this.mapSizeY))
            {
                RoomTile temp = findTile(closedList, (short) (x - 1), (short) (y + 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
                {
                    temp.isDiagonally(true);
                    adj.add(temp);
                }
            }
            if ((x < this.mapSizeX) && (y > 0))
            {
                RoomTile temp = findTile(closedList, (short) (x + 1), (short) (y - 1));
                if (temp != null && (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.x == newX && temp.y == newY && room.canSitOrLayAt(newX, newY))))
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

    @EventHandler
    public static void configurationUpdated(EmulatorConfigUpdatedEvent event)
    {
        MAXIMUM_STEP_HEIGHT = Emulator.getConfig().getDouble("pathfinder.step.maximum.height", 1.1);
        ALLOW_FALLING = Emulator.getConfig().getBoolean("pathfinder.step.allow.falling", true);
    }
}
