package com.habboproject.server.threads.executors.teleport;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.executors.engine.ItemUpdateEvent;
import com.habboproject.server.game.rooms.objects.items.types.floor.teleport.TeleportFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

public class TeleportEventThree implements CometThread {
    private final TeleportFloorItem teleporterOne;
    private final PlayerEntity playerEntity;

    public TeleportEventThree(TeleportFloorItem teleporterOne, PlayerEntity playerEntity) {
        this.teleporterOne = teleporterOne;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        int roomId = ItemManager.getInstance().roomIdByItemId(this.teleporterOne.getPairId());

        Room targetRoom = RoomManager.getInstance().get(roomId);
        if (targetRoom == null) {
            return;
        }

        RoomItemFloor targetTeleport = targetRoom.getItems().getFloorItem(this.teleporterOne.getPairId());
        if (targetTeleport == null) {
            return;
        }

        if (targetRoom.getId() != this.teleporterOne.getRoom().getId()) {
            if (this.playerEntity.getPlayer() != null && this.playerEntity.getPlayer().getSession() != null) {
                this.playerEntity.getPlayer().setTeleportId(targetTeleport.getId());
                this.playerEntity.getPlayer().setTeleportRoomId(roomId);
                this.playerEntity.getPlayer().getSession().send(new RoomForwardMessageComposer(roomId));
            }

            ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.teleporterOne, "0"), 500, TimeUnit.MILLISECONDS);
            return;
        }

        this.playerEntity.warp(targetTeleport.getPosition().copy());
        this.playerEntity.setHeadRotation(targetTeleport.getRotation());
        this.playerEntity.setBodyRotation(targetTeleport.getRotation());

        targetTeleport.setExtraData("2");
        targetTeleport.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.teleporterOne, "0"), 500, TimeUnit.MILLISECONDS);
        ThreadManager.getInstance().executeSchedule(new TeleportEventFour((TeleportFloorItem) targetTeleport, this.playerEntity), 500, TimeUnit.MILLISECONDS);
    }
}

