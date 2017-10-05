package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.types.Room;

public class BedFloorItem extends DefaultFloorItem {
    public BedFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        entity.addStatus(RoomEntityStatus.LAY, this.getDefinition().getHeight() + "");
        entity.markNeedsUpdate();
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        entity.removeStatus(RoomEntityStatus.LAY);
        entity.markNeedsUpdate();
    }
}
