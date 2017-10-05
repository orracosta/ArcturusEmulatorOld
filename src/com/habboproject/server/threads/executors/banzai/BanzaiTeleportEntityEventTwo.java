package com.habboproject.server.threads.executors.banzai;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/02/2017.
 */
public class BanzaiTeleportEntityEventTwo implements CometThread {
    private final RoomItemFloor teleporterOne;
    private final RoomItemFloor teleporterTwo;
    private final RoomEntity entity;

    public BanzaiTeleportEntityEventTwo(RoomItemFloor teleporterOne, RoomItemFloor teleporterTwo, RoomEntity entity) {
        this.teleporterOne = teleporterOne;
        this.teleporterTwo = teleporterTwo;
        this.entity = entity;
    }

    @Override
    public void run() {
        this.entity.getProcessingPath().clear();
        this.entity.warp(this.teleporterTwo.getPosition().copy());

        this.teleporterOne.setExtraData("0");
        this.teleporterTwo.setExtraData("0");

        this.teleporterOne.sendUpdate();
        this.teleporterTwo.sendUpdate();
    }
}
