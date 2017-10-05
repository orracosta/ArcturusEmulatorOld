package com.habboproject.server.game.utilities;

import com.habboproject.server.game.rooms.objects.misc.Position;


public class DistanceCalculator {
    public static int calculate(int pos1X, int pos1Y, int pos2X, int pos2Y) {
        return Math.abs(pos1X - pos2X) + Math.abs(pos1Y - pos2Y);
    }

    public static boolean tilesTouching(int pos1X, int pos1Y, int pos2X, int pos2Y) {
        if (!(Math.abs(pos1X - pos2X) > 1 || Math.abs(pos1Y - pos2Y) > 1)) {
            return true;
        }

        if (pos1X == pos2X && pos1Y == pos2Y) {
            return true;
        }

        return false;
    }

    public static boolean tilesTouching(Position pos1, Position pos2) {
        return tilesTouching(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
    }
}
