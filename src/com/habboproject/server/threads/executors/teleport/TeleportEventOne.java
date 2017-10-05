package com.habboproject.server.threads.executors.teleport;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.threads.executors.engine.ItemUpdateEvent;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class TeleportEventOne implements CometThread {
    private final TeleportFloorItem teleporterOne;
    private final PlayerEntity playerEntity;

    public TeleportEventOne(TeleportFloorItem teleporterOne, PlayerEntity playerEntity) {
        this.teleporterOne = teleporterOne;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        if (this.teleporterOne == null || this.playerEntity == null) {
            return;
        }

        this.playerEntity.setTeleporting(true);
        this.playerEntity.moveTo(this.teleporterOne.getPosition().copy());

        ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.teleporterOne, "0"), 500, TimeUnit.MILLISECONDS);
        ThreadManager.getInstance().executeSchedule(new TeleportEventTwo(this.teleporterOne, this.playerEntity), 1000, TimeUnit.MILLISECONDS);
    }
}

