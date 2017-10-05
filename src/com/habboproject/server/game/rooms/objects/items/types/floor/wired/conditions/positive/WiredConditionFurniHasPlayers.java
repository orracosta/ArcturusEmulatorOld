package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Iterator;
import java.util.List;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionFurniHasPlayers extends WiredConditionItem {
    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param room     The instance of the room
     * @param owner    The ID of the owner
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredConditionFurniHasPlayers(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 8;
    }

    @Override @SuppressWarnings({"unchecked", "deprecation"})
    public boolean evaluate(RoomEntity entity, Object data) {
        int selectedItemsCount = 0;
        int itemsWithUserCount = 0;
        int itemsWithoutUsersCount = 0;

        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                getWiredData().getSelectedIds().remove(itemId);
            } else {
                selectedItemsCount++;

                if (floorItem.getEntitiesOnItem().size() > 0) {
                    itemsWithUserCount++;
                } else {
                    itemsWithoutUsersCount++;
                }
            }
        }

        if (isNegative) {
            if (itemsWithoutUsersCount == selectedItemsCount) {
                return true;
            }

            return false;
        } else {
            return itemsWithUserCount == selectedItemsCount;
        }
    }
}
