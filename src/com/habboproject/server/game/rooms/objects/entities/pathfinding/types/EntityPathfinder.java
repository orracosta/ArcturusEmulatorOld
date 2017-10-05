package com.habboproject.server.game.rooms.objects.entities.pathfinding.types;

import com.habboproject.server.game.rooms.objects.entities.pathfinding.Pathfinder;

public class EntityPathfinder extends Pathfinder {
    private static EntityPathfinder pathfinderInstance;

    public static EntityPathfinder getInstance() {
        if (pathfinderInstance == null) {
            pathfinderInstance = new EntityPathfinder();
        }

        return pathfinderInstance;
    }
}
