package com.habboproject.server.game.rooms.objects.items.types.floor.adjustable;

import com.habboproject.server.game.rooms.objects.items.types.floor.others.SeatFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import org.apache.commons.lang.StringUtils;


public class AdjustableHeightSeatFloorItem extends SeatFloorItem {
    public AdjustableHeightSeatFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getExtraData().isEmpty()) {
            this.setExtraData("0");
        }
    }

    @Override
    public double getSitHeight() {
        double height;

        if (!StringUtils.isNumeric(this.getExtraData())) {
            height = 1.0;
        } else {
            height = Double.parseDouble(this.getExtraData());

            if (height <= 1) {
                height += 1.0;
            } else {
                height += 0.5;
            }
        }

        return height;
    }
}
