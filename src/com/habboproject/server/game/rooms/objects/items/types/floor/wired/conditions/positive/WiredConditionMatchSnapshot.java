package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Iterator;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionMatchSnapshot extends WiredConditionItem {
    private static final int PARAM_MATCH_STATE = 0;
    private static final int PARAM_MATCH_ROTATION = 1;
    private static final int PARAM_MATCH_POSITION = 2;

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
    public WiredConditionMatchSnapshot(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 0;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (this.getWiredData().getParams().size() != 3) {
            return false;
        }

        final boolean matchState = this.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        final boolean matchRotation = this.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        final boolean matchPosition = this.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        for (Iterator localIterator = getWiredData().getSelectedIds().iterator(); localIterator.hasNext();) {
            long itemId = (Long) localIterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                getWiredData().getSelectedIds().remove(itemId);
            } else {
                WiredItemSnapshot snapshot = this.getWiredData().getSnapshots().get(itemId);

                if (snapshot != null) {
                    boolean matchesState = !this.isNegative;
                    boolean matchesRotation = !this.isNegative;
                    boolean matchesPosition = !this.isNegative;

                    if (matchState) {
                        if (!floorItem.getExtraData().equals(snapshot.getExtraData())) {
                            matchesState = false;
                        } else if (this.isNegative) {
                            matchesState = true;
                        }
                    }

                    if (matchRotation) {
                        if (floorItem.getRotation() != snapshot.getRotation()) {
                            matchesRotation = false;
                        } else if (this.isNegative) {
                            matchesRotation = true;
                        }
                    }

                    if (matchPosition) {
                        if (floorItem.getPosition().getX() != snapshot.getX() || floorItem.getPosition().getY() != snapshot.getY()) {
                            matchesPosition = false;
                        } else if (this.isNegative) {
                            matchesPosition = true;
                        }
                    }

                    if (this.isNegative && (matchesPosition || matchesRotation || matchesState)) {
                        return false;
                    } else if (!this.isNegative && (!matchesPosition || !matchesRotation || !matchesState)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
