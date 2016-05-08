package com.eu.habbo.util.pathfinding;

import gnu.trove.set.hash.THashSet;

import java.awt.*;

/**
 * Created on 14-9-2014 13:56.
 */
public class Tile extends Point
{
    public int X;
    public int Y;
    public double Z;

    public Tile()
    {
        super(0, 0);

        this.X = 0;
        this.Y = 0;
        this.Z = 0;
    }

    public Tile(int x, int y)
    {
        super(x, y);

        this.X = x;
        this.Y = y;
        this.Z = 0;
    }

    public Tile(int x, int y, double z)
    {
        super(x, y);

        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Tile && ((Tile) o).X == this.X && ((Tile) o).Y == this.Y;
    }

//    public static THashSet<Tile> getTilesAt(int x, int y, int width, int length, int rotation)
//    {
//        THashSet<Tile> PointList = new THashSet<Tile>();
//        if (x > 1) {
//            if ((rotation == 0) || (rotation == 4)) {
//                for (int i = 1; i < x; i++)
//                {
//                    PointList.add(new Tile(width, length + i, i));
//                    for (int j = 1; j < y; j++) {
//                        PointList.add(new Tile(width + j, length + i, i < j ? j : i));
//                    }
//                }
//            } else if ((rotation == 2) || (rotation == 6)) {
//                for (int i = 1; i < x; i++)
//                {
//                    PointList.add(new Tile(width + i, length, i));
//                    for (int j = 1; j < y; j++) {
//                        PointList.add(new Tile(width + i, length + j, i < j ? j : i));
//                    }
//                }
//            }
//        }
//        if (y > 1) {
//            if ((rotation == 0) || (rotation == 4)) {
//                for (int i = 1; i < y; i++)
//                {
//                    PointList.add(new Tile(width + i, length, i));
//                    for (int j = 1; j < x; j++) {
//                        PointList.add(new Tile(width + i, length + j, i < j ? j : i));
//                    }
//                }
//            } else if ((rotation == 2) || (rotation == 6)) {
//                for (int i = 1; i < y; i++)
//                {
//                    PointList.add(new Tile(width, length + i, i));
//                    for (int j = 1; j < x; j++) {
//                        PointList.add(new Tile(width + j, length + i, i < j ? j : i));
//                    }
//                }
//            }
//        }
//        return PointList;
//    }

    public static THashSet<Tile> getTilesAt(int x, int y, int width, int length, int rotation)
    {
        THashSet<Tile> pointList = new THashSet<Tile>();

        if(rotation == 0 || rotation == 4)
        {
            for (int i = x; i <= (x + (width - 1)); i++)
            {
                for (int j = y; j <= (y + (length - 1)); j++)
                {
                    pointList.add(new Tile(i, j, 0.0));
                }
            }
        }
        else if(rotation == 2 || rotation == 6)
        {
            for (int i = x; i <= (x + (length - 1)); i++)
            {
                for (int j = y; j <= (y + (width - 1)); j++)
                {
                    pointList.add(new Tile(i, j, 0.0));
                }
            }
        }

        return pointList;
    }

    public Tile copy()
    {
        return new Tile(this.X, this.Y, this.Z);
    }
}
