package com.habboproject.server.threads.executors.banzai;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/02/2017.
 */
public class BanzaiTeleportItemEventTwo implements CometThread {
    private final RoomItemFloor teleporterOne;
    private final RoomItemFloor teleporterTwo;
    private final RoomItemFloor floorItem;

    public BanzaiTeleportItemEventTwo(RoomItemFloor teleporterOne, RoomItemFloor teleporterTwo, RoomItemFloor floorItem) {
        this.teleporterOne = teleporterOne;
        this.teleporterTwo = teleporterTwo;
        this.floorItem = floorItem;
    }

    @Override
    public void run() {
        this.floorItem.getPosition().setX(this.teleporterTwo.getPosition().getX());
        this.floorItem.getPosition().setY(this.teleporterTwo.getPosition().getY());

        for (RoomItemFloor floorItem : this.teleporterOne.getRoom().getItems().getItemsOnSquare(this.teleporterTwo.getPosition().getX(), this.teleporterTwo.getPosition().getY())) {
            floorItem.onItemAddedToStack(floorItem);
        }

        this.floorItem.getPosition().setZ(this.teleporterTwo.getPosition().getZ());

        this.teleporterOne.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(this.floorItem));

        this.teleporterOne.setExtraData("0");
        this.teleporterTwo.setExtraData("0");

        this.teleporterOne.sendUpdate();
        this.teleporterTwo.sendUpdate();
    }
}
