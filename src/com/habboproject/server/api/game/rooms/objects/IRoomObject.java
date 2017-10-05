package com.habboproject.server.api.game.rooms.objects;

import com.habboproject.server.api.game.rooms.IRoom;
import com.habboproject.server.api.game.rooms.util.IPosition;

public interface IRoomObject {
    IPosition getPosition();

    boolean isAtDoor();

    IRoom getRoom();
}
