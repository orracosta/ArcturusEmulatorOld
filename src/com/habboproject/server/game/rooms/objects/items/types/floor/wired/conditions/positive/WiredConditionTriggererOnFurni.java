package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Iterator;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionTriggererOnFurni extends WiredConditionItem {
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
    public WiredConditionTriggererOnFurni(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override @SuppressWarnings({"unchecked", "deprecation"}) // mrcr zika do baile pika de mel do role
    public boolean evaluate(RoomEntity entity, Object data) {
        if (entity == null)
            return false;

        boolean triggererOnFurni = false;
        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();
            RoomItemFloor floorItem = getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                getWiredData().getSelectedIds().remove(itemId);
            } else {
                triggererOnFurni = floorItem.getEntitiesOnItem().contains(entity);

                if (triggererOnFurni) break;
            }
        }

        return triggererOnFurni && !isNegative ? true : !triggererOnFurni && isNegative ? true : false;
    }


    @Override
    public int getInterface() {
        return 8;
    }
}
