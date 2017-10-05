package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionDateRangeActive extends WiredConditionItem {
    private final int PARAM_INITIAL_DATE = 0;
    private final int PARAM_FINAL_DATE = 1;

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
    public WiredConditionDateRangeActive(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 24;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (this.getWiredData().getParams() == null || this.getWiredData().getParams().size() == 0) {
            return false;
        }

        if (this.getWiredData().getParams().get(PARAM_INITIAL_DATE) == null) {
            return false;
        }

        final int currentDate = (int) Comet.getTime();

        if (this.getWiredData().getParams().get(PARAM_FINAL_DATE) != null && this.getWiredData().getParams().get(PARAM_FINAL_DATE) != 0) {
            return currentDate >= this.getWiredData().getParams().get(PARAM_INITIAL_DATE) && currentDate < this.getWiredData().getParams().get(PARAM_FINAL_DATE);
        }

        return currentDate >= this.getWiredData().getParams().get(PARAM_INITIAL_DATE);
    }
}
