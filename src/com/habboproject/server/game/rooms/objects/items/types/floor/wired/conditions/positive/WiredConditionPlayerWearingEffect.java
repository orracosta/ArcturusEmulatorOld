package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionPlayerWearingEffect extends WiredConditionItem {
    public static int PARAM_EFFECT_ID = 0;

    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param room     The ID of the room
     * @param owner    The ID of the owner
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredConditionPlayerWearingEffect(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 12;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (entity == null) return false;

        if (this.getWiredData().getParams().size() != 1) {
            return false;
        }

        final int effectId = this.getWiredData().getParams().get(PARAM_EFFECT_ID);
        boolean isWearingEffect = false;

        if (entity.getCurrentEffect() != null) {
            if (entity.getCurrentEffect().getEffectId() == effectId) {
                isWearingEffect = true;
            }
        }

        if (isWearingEffect) {
            if (isNegative) {
                return false;
            }
        } else {
            if (!isNegative) {
                return false;
            }
        }

        return true;
    }
}
