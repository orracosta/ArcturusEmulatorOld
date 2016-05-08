package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 31-8-2014 12:36.
 */
public class RoomLayout {

    private String name;
    private int doorX;
    private int doorY;
    private int doorZ;
    private int doorDirection;
    private String heightmap;
    private String map;

    private int mapSize;
    private int mapSizeX;
    private int mapSizeY;
    private short[][] squareHeights;
    private RoomTileState[][] squareStates;
    private double heighestPoint;

    public RoomLayout(ResultSet set) throws SQLException
    {
        try
        {
            this.name = set.getString("name");
            this.doorX = set.getInt("door_x");
            this.doorY = set.getInt("door_y");

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
        String[] modelTemp = this.heightmap.split(Character.toString('\r'));

        this.mapSize = 0;
        this.mapSizeX = modelTemp[0].length();
        this.mapSizeY = modelTemp.length;
        this.squareHeights = new short[this.mapSizeX][this.mapSizeY];
        this.squareStates = new RoomTileState[this.mapSizeX][this.mapSizeY];

        int x;
        String Square;
        short height;
        for (int y = 0; y < this.mapSizeY; y++)
        {
            if(modelTemp[y].isEmpty() || modelTemp[y].equalsIgnoreCase("\r"))
                continue;

            if (y > 0) {
                modelTemp[y] = modelTemp[y].substring(1);
            }
            for (x = 0; x < this.mapSizeX; x++)
            {
                if(modelTemp[y].length() != this.mapSizeX)
                    break;

                Square = modelTemp[y].substring(x, x + 1).trim().toLowerCase();
                if (Square.equals("x"))
                {
                    this.squareStates[x][y] = RoomTileState.BLOCKED;
                    this.mapSize += 1;
                }
                else
                {
                    if (Square.isEmpty()) {
                        height = 0;
                    }
                    else if (Emulator.isNumeric(Square))
                    {
                        height = Short.parseShort(Square);
                    }
                    else
                    {
                        height = (short) (10 + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(Square.toUpperCase()));
                    }
                    this.squareStates[x][y] = RoomTileState.OPEN;
                    this.squareHeights[x][y] = height;
                    this.mapSize += 1;
                    if (this.heighestPoint < height) {
                        this.heighestPoint = height;
                    }
                }
//                if ((this.doorX == x) && (this.doorY == y))
//                {
//                    this.squareStates[x][y] = RoomTileState.OPEN;
//                    this.doorZ = (int)this.squareHeights[x][y];
//                }
            }
        }

        for (String MapLine : this.heightmap.split("\r\n"))
        {
            if ((MapLine != null) && (!MapLine.isEmpty()))
            {
                this.map = (this.map + MapLine + '\r');
            }
        }

        Tile doorFrontTile = PathFinder.getSquareInFront(this.doorX, this.doorY, this.doorDirection);

        if(this.tileExists(doorFrontTile.x, doorFrontTile.y))
        {
            if(this.getSquareStates()[doorFrontTile.x][doorFrontTile.y] != RoomTileState.BLOCKED)
            {
                if (this.doorZ != (int)this.squareHeights[doorFrontTile.x][doorFrontTile.y])
                {
                    this.doorZ = (int) this.squareHeights[doorFrontTile.x][doorFrontTile.y];
                    this.squareStates[this.doorX][this.doorY] = this.squareStates[doorFrontTile.x][doorFrontTile.y];
                    this.squareHeights[this.doorX][this.doorY] = this.squareHeights[doorFrontTile.x][doorFrontTile.y];

                    StringBuilder stringBuilder = new StringBuilder(this.heightmap);
                    stringBuilder.setCharAt((this.doorX * (this.getMapSizeX() + 2)) + this.doorY, this.squareHeights[doorFrontTile.x][doorFrontTile.y] > 10 ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(this.squareHeights[doorFrontTile.x][doorFrontTile.y] - 10) : ("" + (int) (this.squareHeights[doorFrontTile.x][doorFrontTile.y])).charAt(0));
                    this.heightmap = stringBuilder.toString();
                }
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public int getDoorX()
    {
        return this.doorX;
    }

    public void setDoorX(int doorX)
    {
        this.doorX = doorX;
    }

    public int getDoorY()
    {
        return this.doorY;
    }

    public void setDoorY(int doorY)
    {
        this.doorY = doorY;
    }

    public int getDoorZ()
    {
        return this.doorZ;
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

        return this.squareHeights[x][y];
    }

    public boolean tileExists(int x, int y)
    {
        return !(x < 0 || y < 0 || x >= this.getMapSizeX() || y >= this.getMapSizeY());
    }

    public boolean tileWalkable(int x, int y)
    {
        return this.tileExists(x, y) && this.getSquareStates()[x][y] == RoomTileState.OPEN;
    }

    public RoomTileState[][] getSquareStates()
    {
        return this.squareStates;
    }

    public String getRelativeMap()
    {
        return this.heightmap.replace("\r\n", "\r");
    }
}
