package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Iterator;
import java.util.List;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionHasFurniOn extends WiredConditionItem {
    private final static int PARAM_MODE = 0;

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
    public WiredConditionHasFurniOn(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 7;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        int mode = 0;

        if (this.getWiredData().getParams() != null && this.getWiredData().getParams().containsKey(PARAM_MODE)) {
            mode = this.getWiredData().getParams().get(PARAM_MODE);
        }

        int selectedItemsCount = 0;
        int selectedItemsWithFurni = 0;

        List<Long> toRemove = Lists.newArrayList();
        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
            } else {
                selectedItemsCount++;
                for (RoomItemFloor itemOnSq : floorItem.getItemsOnStack()) {
                    if (itemOnSq.getPosition().getZ() != 0.0 && itemOnSq.getPosition().getZ() > floorItem.getPosition().getZ() && itemOnSq.getId() != floorItem.getId()) {
                        selectedItemsWithFurni++;
                        break;
                    }
                }
            }
        }

        if (toRemove.size() > 0) {
            for (Long itemId : toRemove) {
                getWiredData().getSelectedIds().remove(itemId);
            }
        }

        boolean result = false;

        if(mode == 0) {
            if(selectedItemsWithFurni >= 1) result = true;
        } else {
            if(selectedItemsWithFurni == selectedItemsCount) result = true;
        }

        return this.isNegative ? !result : result;
    }
}
