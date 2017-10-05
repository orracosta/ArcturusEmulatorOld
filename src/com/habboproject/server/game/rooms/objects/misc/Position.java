package com.habboproject.server.game.rooms.objects.misc;

import com.habboproject.server.api.game.rooms.util.IPosition;
import com.habboproject.server.game.rooms.objects.RoomFloorObject;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;


public class Position implements IPosition {
    private int x;
    private int y;
    private double z;

    public Position(int x, int y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Position old) {
        this.x = old.getX();
        this.y = old.getY();
        this.z = old.getZ();
    }

    public Position() {
        this.x = 0;
        this.y = 0;
        this.z = 0d;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0d;
    }

    public Position add(Position other) {
        return new Position(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
    }

    public Position subtract(Position other) {
        return new Position(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
    }

    public int getDistanceSquared(Position point) {
        int dx = this.getX() - point.getX();
        int dy = this.getY() - point.getY();

        return (dx * dx) + (dy * dy);
    }

    public static String validateWallPosition(String position) {
        try {
            String[] data = position.split(" ");
            if (data[2].equals("l") || data[2].equals("r")) {
                String[] width = data[0].substring(3).split(",");
                int widthX = Integer.parseInt(width[0]);
                int widthY = Integer.parseInt(width[1]);
//                if (widthX < 0 || widthY < 0 || widthX > 200 || widthY > 200)
//                    return null;

                String[] length = data[1].substring(2).split(",");
                int lengthX = Integer.parseInt(length[0]);
                int lengthY = Integer.parseInt(length[1]);
//                if (lengthX < 0 || lengthY < 0 || lengthX > 200 || lengthY > 200)
//                    return null;

                return ":w=" + widthX + "," + widthY + " " + "l=" + lengthX + "," + lengthY + " " + data[2];
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    public static double calculateHeight(RoomItemFloor item) {
        if (item.getDefinition().getInteraction().equals("gate")) {
            return 0;
        } else if (item.getDefinition().canSit()) {
            return 0;
        }

        return item.getDefinition().getHeight();
    }

    public static int calculateRotation(Position from, Position to) {
        return calculateRotation(from.x, from.y, to.x, to.y, false);
    }

    public static int calculateRotation(int x, int y, int newX, int newY, boolean reversed) {
        int rotation = 0;

        if (x > newX && y > newY)
            rotation = 7;
        else if (x < newX && y < newY)
            rotation = 3;
        else if (x > newX && y < newY)
            rotation = 5;
        else if (x < newX && y > newY)
            rotation = 1;
        else if (x > newX)
            rotation = 6;
        else if (x < newX)
            rotation = 2;
        else if (y < newY)
            rotation = 4;
        else if (y > newY)
            rotation = 0;

        if (reversed) {
            if (rotation > 3) {
                rotation = rotation - 4;
            } else {
                rotation = rotation + 4;
            }
        }

        return rotation;
    }

    public Position squareInFront(int angle) {
        return calculatePosition(this.x, this.y, angle, false);
    }

    public Position squareBehind(int angle) {
        return calculatePosition(this.x, this.y, angle, true);
    }

    public static Position calculatePosition(int x, int y, int angle, boolean isReversed) {
        switch (angle) {
            case 0:
                if (!isReversed)
                    y--;
                else
                    y++;
                break;

            case 1:
                if (!isReversed) {
                    x++;
                    y--;
                } else {
                    x--;
                    y++;
                }
                break;

            case 2:
                if (!isReversed)
                    x++;
                else
                    x--;
                break;

            case 3:
                if (!isReversed) {
                    x++;
                    y++;
                } else {
                    x--;
                    y--;
                }
                break;

            case 4:
                if (!isReversed)
                    y++;
                else
                    y--;
                break;

            case 5:
                if (!isReversed) {
                    x--;
                    y++;
                } else {
                    x++;
                    y--;
                }
                break;

            case 6:
                if (!isReversed)
                    x--;
                else
                    x++;
                break;

            case 7:
                if (!isReversed) {
                    x--;
                    y--;
                } else {
                    x++;
                    y++;
                }
                break;
        }

        return new Position(x, y);
    }

    public double distanceTo(IPosition pos) {
        return Math.abs(this.getX() - pos.getX()) + Math.abs(this.getY() - pos.getY());
    }

    public double distanceTo(RoomFloorObject roomFloorObject) {
        return distanceTo(roomFloorObject.getPosition());
    }

    public boolean touching(Position pos) {
        if (!(Math.abs(this.getX() - pos.getX()) > 1 || Math.abs(this.getY() - pos.getY()) > 1)) {
            return true;
        }

        if (this.getX() == pos.getX() && this.getY() == pos.getY()) {
            return true;
        }

        return false;
    }

    public boolean touching(RoomFloorObject roomFloorObject) {
        return this.touching(roomFloorObject.getPosition());
    }

    public Position copy() {
        return new Position(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")";
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            return ((Position) o).getX() == this.getX() && ((Position) o).getY() == this.getY();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
