package com.habboproject.server.threads.executors.teleport;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class TeleportEventTwo implements CometThread {
    private final TeleportFloorItem teleporterOne;
    private final PlayerEntity playerEntity;

    public TeleportEventTwo(TeleportFloorItem teleporterOne, PlayerEntity playerEntity) {
        this.teleporterOne = teleporterOne;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        this.teleporterOne.setExtraData("2");
        this.teleporterOne.sendUpdate();

        RoomItemFloor pairItem = this.teleporterOne.getPartner(this.teleporterOne.getPairId());
        if (pairItem == null) {
            int roomId = ItemManager.getInstance().roomIdByItemId(this.teleporterOne.getPairId());
            if (RoomManager.getInstance().get(roomId) == null) {
                ThreadManager.getInstance().executeSchedule(new TeleportEventFour(this.teleporterOne, this.playerEntity), 500, TimeUnit.MILLISECONDS);
                return;
            }
        }

        ThreadManager.getInstance().executeSchedule(new TeleportEventThree(this.teleporterOne, this.playerEntity), 500, TimeUnit.MILLISECONDS);
    }
}

