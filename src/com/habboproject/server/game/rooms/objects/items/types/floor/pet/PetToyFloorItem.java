package com.habboproject.server.game.rooms.objects.items.types.floor.pet;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.item.FloorItemUpdateStateEvent;

import java.util.concurrent.TimeUnit;

public class PetToyFloorItem extends DefaultFloorItem {
    public PetToyFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if(entity == null || !(entity instanceof PetEntity)) {
            return;
        }

        entity.setHeadRotation(this.getRotation());
        entity.setBodyRotation(this.getRotation());

        entity.addStatus(RoomEntityStatus.PLAY, "");
        entity.markNeedsUpdate();

        ThreadManager.getInstance().executeSchedule(new FloorItemUpdateStateEvent(this, "1"), 250, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if(!(entity instanceof PetEntity)) {
            return;
        }

        entity.removeStatus(RoomEntityStatus.PLAY);
        entity.markNeedsUpdate();

        this.setExtraData("0");
        this.sendUpdate();
    }
}
