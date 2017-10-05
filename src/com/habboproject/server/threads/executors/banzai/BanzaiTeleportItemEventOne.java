package com.habboproject.server.threads.executors.banzai;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class BanzaiTeleportItemEventOne implements CometThread {
    private final RoomItemFloor teleporterOne;
    private final RoomItemFloor teleporterTwo;
    private final RoomItemFloor floorItem;

    public BanzaiTeleportItemEventOne(RoomItemFloor teleporterOne, RoomItemFloor teleporterTwo, RoomItemFloor floorItem) {
        this.teleporterOne = teleporterOne;
        this.teleporterTwo = teleporterTwo;
        this.floorItem = floorItem;
    }

    @Override
    public void run() {
        this.teleporterOne.setExtraData("1");
        this.teleporterTwo.setExtraData("1");

        this.teleporterOne.sendUpdate();
        this.teleporterTwo.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new BanzaiTeleportItemEventTwo(this.teleporterOne, this.teleporterTwo, this.floorItem), 500, TimeUnit.MILLISECONDS);
    }
}
