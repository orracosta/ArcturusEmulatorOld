package com.habboproject.server.threads.executors.freeze;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.executors.engine.ItemUpdateEvent;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeTileThrowBallEvent implements CometThread {
    private final RoomItemFloor floorItem;
    private final PlayerEntity entity;
    private final int radius;

    public FreezeTileThrowBallEvent(RoomItemFloor floorItem, PlayerEntity entity) {
        this.floorItem = floorItem;
        this.entity = entity;
        this.radius = entity.getPlayer().getFreeze().getBoost();
    }

    @Override
    public void run() {
        this.entity.getPlayer().getFreeze().decreaseBall();

        this.floorItem.setExtraData("" + this.radius * 1000);
        this.floorItem.sendUpdate();

        ThreadManager.getInstance().executeSchedule(new FreezeTileBallExplosionEvent(this.floorItem, this.entity, this.radius), 2000, TimeUnit.MILLISECONDS);
        ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.floorItem, "11000"), 2000, TimeUnit.MILLISECONDS);
        ThreadManager.getInstance().executeSchedule(new ItemUpdateEvent(this.floorItem, "0"), 3000, TimeUnit.MILLISECONDS);
    }
}
