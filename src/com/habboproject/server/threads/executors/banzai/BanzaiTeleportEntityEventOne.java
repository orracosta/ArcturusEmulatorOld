package com.habboproject.server.threads.executors.banzai;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class BanzaiTeleportEntityEventOne implements CometThread {
    private final RoomItemFloor teleporterOne;
    private final RoomItemFloor teleporterTwo;
    private final RoomEntity entity;

    public BanzaiTeleportEntityEventOne(RoomItemFloor teleporterOne, RoomItemFloor teleporterTwo, RoomEntity entity) {
        this.teleporterOne = teleporterOne;
        this.teleporterTwo = teleporterTwo;
        this.entity = entity;
    }

    @Override
    public void run() {
        this.teleporterOne.setExtraData("1");
        this.teleporterTwo.setExtraData("1");

        this.teleporterOne.sendUpdate();
        this.teleporterTwo.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new BanzaiTeleportEntityEventTwo(this.teleporterOne, this.teleporterTwo, this.entity), 500, TimeUnit.MILLISECONDS);
    }
}
