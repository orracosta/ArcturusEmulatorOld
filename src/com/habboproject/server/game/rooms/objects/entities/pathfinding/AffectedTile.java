package com.habboproject.server.game.rooms.objects.entities.pathfinding;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class AffectedTile {
    public int x;
    public int y;

    public AffectedTile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static List<AffectedTile> getAffectedBothTilesAt(int length, int width, int posX, int posY, int rotation) {
        List<AffectedTile> pointList = new ArrayList<>();

        pointList.add(new AffectedTile(posX, posY));

        if (length > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            }
        }

        if (width > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            }
        }

        return pointList;
    }

    public static List<AffectedTile> getAffectedTilesAt(int length, int width, int posX, int posY, int rotation) {
        List<AffectedTile> pointList = new ArrayList<>();

        if (length > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < length; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < width; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            }
        }

        if (width > 1) {
            if (rotation == 0 || rotation == 4) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX + i, posY));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + i, posY + j));
                    }
                }
            } else if (rotation == 2 || rotation == 6) {
                for (int i = 1; i < width; i++) {
                    pointList.add(new AffectedTile(posX, posY + i));

                    for (int j = 1; j < length; j++) {
                        pointList.add(new AffectedTile(posX + j, posY + i));
                    }
                }
            }
        }

        return pointList;
    }

    public static List<AffectedTile> getAffectedTilesByExplosion(int radius, RoomItemFloor floorItem, boolean isDiagonal) {
        ArrayList<AffectedTile> affectedTiles = Lists.newArrayList();

        affectedTiles.add(new AffectedTile(floorItem.getPosition().getX(), floorItem.getPosition().getY()));

        int i = 0;
        while (i < 4) {
            Position position = floorItem.getPosition().copy();

            int j = 0;
            while (j < radius) {
                int rotation = !isDiagonal ? i * 2 : i * 2 + 1;

                position = position.squareInFront(rotation);

                if (floorItem.getRoom().getMapping().isValidPosition(position)) {
                    affectedTiles.add(new AffectedTile(position.getX(), position.getY()));
                }

                ++j;
            }

            ++i;
        }

        return affectedTiles;
    }

    public static boolean tilesAdjecent(Position one, Position two) {
        return AffectedTile.tilesAdjecent(one.getX(), one.getY(), two.getX(), two.getY());
    }

    public static boolean tilesAdjecent(int x1, int y1, int x2, int y2) {
        if (!(Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1 || x1 == x2 && y1 == y2)) {
            return false;
        }

        return true;
    }
}