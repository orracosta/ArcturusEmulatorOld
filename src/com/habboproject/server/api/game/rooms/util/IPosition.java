package com.habboproject.server.api.game.rooms.util;

public interface IPosition {
    int getX();

    int getY();

    double getZ();

    void setX(int x);

    void setY(int y);

    void setZ(double z);

    IPosition squareInFront(int angle);

    IPosition squareBehind(int angle);

    double distanceTo(IPosition position);
}
