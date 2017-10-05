package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionTimeLessThan extends WiredConditionItem {
    private static final int PARAM_TICKS = 0;

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
    public WiredConditionTimeLessThan(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 3;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (this.getWiredData().getParams() == null || this.getWiredData().getParams().size() == 0 || this.getWiredData().getParams().get(PARAM_TICKS) == null)
            return false;

        final int ticks = this.getWiredData().getParams().get(PARAM_TICKS);
        return this.getRoom().getWiredTimer() <= ticks;
    }
}
