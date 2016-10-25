package com.eu.habbo.util.pathfinding;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PathFinder
{
    private Room room;
    private RoomUnit roomUnit;
    private Queue<Node> path;

    public PathFinder(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
        this.room = null;
    }

    public PathFinder(Room room, RoomUnit roomUnit)
    {
        this.room = room;
        this.roomUnit = roomUnit;
    }

    public void findPath()
    {
        this.path = this.calculatePath();
    }

    Queue<Node> calculatePath()
    {
        if(this.room != null && this.roomUnit != null) {
            GameMap<Node> gameMap = this.room.getGameMap();
            if (gameMap != null) {

                Queue<Node> nodeQueue = gameMap.findPath(this.roomUnit.getX(), this.roomUnit.getY(), this.roomUnit.getGoal().x, this.roomUnit.getGoal().y, room);

                if (!nodeQueue.isEmpty()) {
                    try {
                        gameMap.finalize();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    return nodeQueue;
                } else {


                    return new LinkedList<Node>();
                }
            }
        }
        return new LinkedList<Node>();
    }

    public RoomUnit getRoomUnit() {
        return roomUnit;
    }

    public void setRoomUnit(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Queue<Node> getPath() {
        return path;
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
        return tilesAdjecent(one.x, one.y, two.x, two.y);
    }

    public static boolean tilesAdjecent(int x1, int y1, int x2, int y2)
    {
        return !(Math.abs(x1 - x2) > 1) && !(Math.abs(y1 - y2) > 1);
    }

    public static RoomTile getSquareInFront(RoomLayout roomLayout, short x, short y, int rotation)
    {
        return getSquareInFront(roomLayout, x, y, rotation, (short)1);
    }

    public static RoomTile getSquareInFront(RoomLayout roomLayout, short x, short y, int rotation, short offset)
    {
        rotation = rotation % 8;

        if(rotation == 0)
            return roomLayout.getTile(x, (short) (y - offset));
        else if(rotation == 1)
            return roomLayout.getTile((short) (x + offset), (short) (y - offset));
        else if(rotation == 2)
            return roomLayout.getTile((short) (x + offset), y);
        else if(rotation == 3)
            return roomLayout.getTile((short) (x + offset), (short) (y + offset));
        else if(rotation == 4)
            return roomLayout.getTile(x, (short) (y + offset));
        else if(rotation == 5)
            return roomLayout.getTile((short) (x - offset), (short) (y + offset));
        else if(rotation == 6)
            return roomLayout.getTile((short) (x - offset), y);
        else if(rotation == 7)
            return roomLayout.getTile((short) (x - offset), (short) (y - offset));
        else
            return roomLayout.getTile(x, y);
    }

    public static List<RoomTile> getTilesAround(RoomLayout roomLayout, short x, short y)
    {
        List<RoomTile> tiles = new ArrayList<RoomTile>();

        for(int i = 0; i < 8; i++)
        {
            tiles.add(getSquareInFront(roomLayout, x, y, i));
        }

        return tiles;
    }

    public static Rectangle getSquare(int x, int y, int width, int length, int rotation)
    {
        rotation = (rotation % 8);

        if(rotation == 2 || rotation == 6)
        {
            return new Rectangle(x, y, length, width);
        }

        return new Rectangle(x, y, width, length);
    }

    public static THashSet<RoomTile> getTilesAt(RoomLayout layout, short x, short y, int width, int length, int rotation)
    {
        THashSet<RoomTile> pointList = new THashSet<RoomTile>();

        if(rotation == 0 || rotation == 4)
        {
            for (short i = x; i <= (x + (width - 1)); i++)
            {
                for (short j = y; j <= (y + (length - 1)); j++)
                {
                    RoomTile t = layout.getTile(i, j);

                    if (t != null)
                    {
                        pointList.add(t);
                    }
                }
            }
        }
        else if(rotation == 2 || rotation == 6)
        {
            for (short i = x; i <= (x + (length - 1)); i++)
            {
                for (short j = y; j <= (y + (width - 1)); j++)
                {
                    RoomTile t = layout.getTile(i, j);

                    if (t != null)
                    {
                        pointList.add(t);
                    }
                }
            }
        }

        return pointList;
    }

    public static boolean tilesAdjecent(RoomTile tile, RoomTile comparator, int width, int length, int rotation)
    {
        Rectangle rectangle = getSquare(comparator.x, comparator.y, width, length, rotation);
        rectangle = new Rectangle(rectangle.x - 1, rectangle.y -1, rectangle.width + 2, rectangle.height + 2);

        return rectangle.contains(tile.x, tile.y);
    }
}
