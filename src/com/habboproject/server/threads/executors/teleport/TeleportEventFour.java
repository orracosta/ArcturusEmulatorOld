package com.habboproject.server.threads.executors.teleport;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class TeleportEventFour implements CometThread {
    private final TeleportFloorItem teleporterTwo;
    private final PlayerEntity playerEntity;

    public TeleportEventFour(TeleportFloorItem teleporterTwo, PlayerEntity playerEntity) {
        this.teleporterTwo = teleporterTwo;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        this.teleporterTwo.setExtraData("1");
        this.teleporterTwo.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new TeleportEventFive(this.teleporterTwo, this.playerEntity), 500, TimeUnit.MILLISECONDS);
    }
}

