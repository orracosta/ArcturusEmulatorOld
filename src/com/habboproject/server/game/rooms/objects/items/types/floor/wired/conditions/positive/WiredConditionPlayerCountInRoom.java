package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionPlayerCountInRoom extends WiredConditionItem {
    private static final int PARAM_AT_LEAST = 0;
    private static final int PARAM_NO_MORE_THAN = 1;

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
    public WiredConditionPlayerCountInRoom(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 5;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (this.getWiredData().getParams().size() != 2) return true;

        final int playerCount = this.getRoom().getEntities().getPlayerEntities().size();

        if (playerCount >= this.getWiredData().getParams().get(PARAM_AT_LEAST) && playerCount <= this.getWiredData().getParams().get(PARAM_NO_MORE_THAN)) {
            // true if is not negative, false if is negative
            return !this.isNegative;
        }

        // false if is not negative, true if is negative.
        return this.isNegative;
    }
}
