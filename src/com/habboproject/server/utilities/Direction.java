package com.habboproject.server.utilities;

public enum Direction {
    North,
    NorthEast,
    East,
    SouthEast,
    South,
    SouthWest,
    West,
    NorthWest;

    public final int num;
    public final int modX;
    public final int modY;

    public static final Direction[] VALUES = Direction.values();
    public static final Direction NEUTRAL = Direction.East;

    Direction() {
        this.num = this.ordinal();
        switch (this.num) {
            // North
            case 0:
                this.modX = 0;
                this.modY = -1;
                break;

            // NorthEast
            case 1:
                this.modX = +1;
                this.modY = -1;
                break;

            // East
            case 2:
                this.modX = +1;
                this.modY = 0;
                break;

            // SouthEast
            case 3:
                this.modX = +1;
                this.modY = +1;
                break;

            // South
            case 4:
                this.modX = 0;
                this.modY = +1;
                break;

            // SouthWest
            case 5:
                this.modX = -1;
                this.modY = +1;
                break;

            // West
            case 6:
                this.modX = -1;
                this.modY = 0;
                break;

            // NorthWest
            case 7:
                this.modX = -1;
                this.modY = -1;
                break;

            // Uh...
            default:
                this.modX = 0;
                this.modY = 0;
        }
    }

    public final Direction invert() {
        return VALUES[(this.num + (VALUES.length / 2)) % VALUES.length];
    }

    public final Direction transform(Direction dir) {
        return VALUES[(this.num + dir.num) % VALUES.length];
    }

    public static final Direction get(int num) {
        return VALUES[num];
    }

    public static final Direction random() {
        return VALUES[RandomInteger.getRandom(0, 7)];
    }

    public static final Direction calculate(int x, int y, int x2, int y2) {
        if (x > x2) {
            if (y == y2) return West;
            else if (y < y2) return SouthWest;
            else return NorthWest;
        } else if (x < x2) {
            if (y == y2) return East;
            else if (y < y2) return SouthEast;
            else return NorthEast;
        } else {
            if (y < y2) return South;
            else return North;
        }
    }
}
