package com.eu.habbo.util.pathfinding;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

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

                Queue<Node> nodeQueue = gameMap.findPath(this.roomUnit.getX(), this.roomUnit.getY(), this.roomUnit.getGoalX(), this.roomUnit.getGoalY(), room);

                if (nodeQueue.size() > 0) {
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

    public static boolean tilesAdjecent(Tile one, Tile two)
    {
        return tilesAdjecent(one.x, one.y, two.x, two.y);
    }

    public static boolean tilesAdjecent(int x1, int y1, int x2, int y2)
    {
        return !(Math.abs(x1 - x2) > 1) && !(Math.abs(y1 - y2) > 1);
    }

    public static Tile getSquareInFront(int x, int y, int rotation)
    {
        return getSquareInFront(x, y, rotation, 1);
    }

    public static Tile getSquareInFront(int x, int y, int rotation, int offset)
    {
        rotation = rotation % 8;

        if(rotation == 0)
            return new Tile(x, y - offset, 0);
        else if(rotation == 1)
            return new Tile(x + offset, y - offset, 0);
        else if(rotation == 2)
            return new Tile(x + offset, y, 0);
        else if(rotation == 3)
            return new Tile(x + offset, y + offset, 0);
        else if(rotation == 4)
            return new Tile(x, y + offset, 0);
        else if(rotation == 5)
            return new Tile(x - offset, y + offset, 0);
        else if(rotation == 6)
            return new Tile(x - offset, y, 0);
        else if(rotation == 7)
            return new Tile(x - offset, y - offset, 0);
        else
            return new Tile(x, y, 0);
    }

    public static List<Tile> getTilesAround(int x, int y)
    {
        List<Tile> tiles = new ArrayList<Tile>();

        for(int i = 0; i < 8; i++)
        {
            tiles.add(getSquareInFront(x, y, i));
        }

        return tiles;
    }

    public static Rectangle getSquare(int x, int y, int width, int length, int rotation)
    {
        rotation = (rotation % 8);
        /*if(rotation == 6)
        {
            System.out.println((x) + "|" + (y-width) + "|" + length + "|" + width);
            return new Rectangle(x, y - width, length, width);
        }
        else if(rotation == 4)
        {
            System.out.println((x - width) + "|" + (y - length) + "|" + width + "|" + length);
            return new Rectangle(x - width, y - length, width, length);
        }*/
        if(rotation == 2 || rotation == 6)
        {
            return new Rectangle(x, y, length, width);
        }

        return new Rectangle(x, y, width, length);
    }

    public static boolean tilesAdjecent(Tile tile, Tile comparator, int width, int length, int rotation)
    {
        Rectangle rectangle = getSquare(comparator.X, comparator.Y, width, length, rotation);
        rectangle = new Rectangle(rectangle.x - 1, rectangle.y -1, rectangle.width + 2, rectangle.height + 2);

        return rectangle.contains(tile);
    }
}
