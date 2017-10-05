package com.habboproject.server.utilities.comparators;

import com.habboproject.server.game.rooms.objects.RoomFloorObject;
import com.habboproject.server.game.rooms.objects.RoomObject;

import java.util.Comparator;


public class PositionComporator implements Comparator<RoomFloorObject> {
    private RoomObject roomFloorObject;

    public PositionComporator(RoomObject roomFloorObject) {
        this.roomFloorObject = roomFloorObject;
    }

    @Override
    public int compare(RoomFloorObject o1, RoomFloorObject o2) {
        final double distanceOne = o1.getPosition().distanceTo(this.roomFloorObject.getPosition());
        final double distanceTwo = o2.getPosition().distanceTo(this.roomFloorObject.getPosition());

        if (distanceOne > distanceTwo)
            return 1;
        else if (distanceOne < distanceTwo)
            return -1;

        return 0;
    }
}
