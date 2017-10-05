package com.habboproject.server.game.rooms.objects.items.types.floor.wired.base;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.wired.dialog.WiredConditionMessageComposer;


public abstract class WiredConditionItem extends WiredFloorItem {
    protected boolean isNegative;

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
    public WiredConditionItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.isNegative = this.getClass().getSimpleName().startsWith("WiredNegativeCondition");
    }

    public boolean isNegative() {
        return isNegative;
    }

    @Override
    public MessageComposer getDialog() {
        return new WiredConditionMessageComposer(this);
    }
}
