package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Iterator;
import java.util.List;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionStuffIs extends WiredConditionItem {
    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param room     The instance of the room
     * @param owner    The ID of the owner
     * @param groupId
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredConditionStuffIs(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 8;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        RoomItemFloor item = null;
        if (data == null || !(data instanceof RoomItemFloor)) {
            if (entity != null && entity instanceof PlayerEntity) {
                List<RoomItemFloor> floorItems = this.getRoom().getItems().getItemsOnSquare(entity.getPosition().getX(), entity.getPosition().getY());
                if (floorItems != null && floorItems.size() > 0) {
                    item = floorItems.get(0);
                }
            }
        } else {
            item = (RoomItemFloor)data;
        }

        if (item == null) {
            return false;
        }

        if (this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().size() == 0) {
            return false;
        }

        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                getWiredData().getSelectedIds().remove(itemId);
            } else {
                if (!isNegative && item.getDefinition().getItemName() == floorItem.getDefinition().getItemName() || isNegative && item.getDefinition().getItemName() != floorItem.getDefinition().getItemName()) {
                    return true;
                }

                if (!isNegative && item.getDefinition().getInteraction() != "default" && item.getDefinition().getInteraction() == floorItem.getDefinition().getInteraction() ||
                        isNegative && item.getDefinition().getInteraction() != "default" && item.getDefinition().getInteraction() != floorItem.getDefinition().getInteraction()) {
                    return true;
                }
            }
        }

        return false;
    }
}
