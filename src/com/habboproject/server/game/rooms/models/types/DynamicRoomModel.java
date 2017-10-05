package com.habboproject.server.game.rooms.models.types;

import com.habboproject.server.game.rooms.models.RoomModel;


public class DynamicRoomModel extends RoomModel {
    public DynamicRoomModel(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) throws InvalidModelException {
        super(name, heightmap, doorX, doorY, doorZ, doorRotation, wallHeight);
    }

    public static DynamicRoomModel create(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) {
        try {
            return new DynamicRoomModel(name, heightmap, doorX, doorY, doorZ, doorRotation, wallHeight);
        } catch (InvalidModelException e) {
            return null;
        }
    }
}
