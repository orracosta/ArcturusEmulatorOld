package com.habboproject.server.threads.executors.teleport;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.threads.executors.engine.ItemUpdateEvent;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class TeleportEventFive implements CometThread {
    private final TeleportFloorItem teleporterTwo;
    private final PlayerEntity playerEntity;

    public TeleportEventFive(TeleportFloorItem teleporterTwo, PlayerEntity playerEntity) {
        this.teleporterTwo = teleporterTwo;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        this.playerEntity.moveTo(this.teleporterTwo.getPosition().squareInFront(this.teleporterTwo.getRotation()));
        this.playerEntity.setOverriden(false);

        ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.teleporterTwo, "0"), 1000, TimeUnit.MILLISECONDS);

        this.playerEntity.setTeleporting(false);
    }
}

