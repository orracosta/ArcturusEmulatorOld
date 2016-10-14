package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.util.pathfinding.PathFinder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomLayout
{
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

    public RoomLayout(ResultSet set) throws SQLException
    {
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
        String[] modelTemp = this.heightmap.split(Character.toString('\r'));

        this.mapSize = 0;
        this.mapSizeX = modelTemp[0].length();
        this.mapSizeY = modelTemp.length;
        this.roomTiles = new RoomTile[this.mapSizeX][this.mapSizeY];

        for (short y = 0; y < this.mapSizeY; y++)
        {
            if(modelTemp[y].isEmpty() || modelTemp[y].equalsIgnoreCase("\r"))
                continue;

            if (y > 0) {
                modelTemp[y] = modelTemp[y].substring(1);
            }
            for (short x = 0; x < this.mapSizeX; x++)
            {
                if(modelTemp[y].length() != this.mapSizeX)
                    break;

                String square = modelTemp[y].substring(x, x + 1).trim().toLowerCase();
                RoomTileState state = RoomTileState.OPEN;
                short height = 0;
                if (square.equals("x"))
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

                this.roomTiles[x][y] = new RoomTile(x, y, height, state);
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
                    //this.roomTiles[this.doorX][this.doorY].z = this.doorZ;

                    StringBuilder stringBuilder = new StringBuilder(this.heightmap);
                    stringBuilder.setCharAt((this.doorY * (this.getMapSizeX() + 2)) + this.doorX, this.roomTiles[doorFrontTile.x][doorFrontTile.y].z >= 10 ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(this.roomTiles[doorFrontTile.x][doorFrontTile.y].z - 10) : ("" + (this.roomTiles[doorFrontTile.x][doorFrontTile.y]).z).charAt(0));
                    this.heightmap = stringBuilder.toString();

                    try
                    {
                        PreparedStatement statement;

                        if (name.startsWith("custom_"))
                        {
                            statement = Emulator.getDatabase().prepare("UPDATE room_models_custom SET heightmap = ? WHERE name = ?");
                        }
                        else
                        {
                            statement = Emulator.getDatabase().prepare("UPDATE room_models SET heightmap = ? WHERE name = ?");
                        }

                        statement.setString(1, this.heightmap);
                        statement.setString(2, this.name);
                        statement.execute();
                        statement.getConnection().close();
                        statement.close();
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
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
}
