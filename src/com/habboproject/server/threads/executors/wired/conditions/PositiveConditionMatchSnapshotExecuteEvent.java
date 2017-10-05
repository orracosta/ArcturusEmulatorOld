package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class PositiveConditionMatchSnapshotExecuteEvent implements Callable<Boolean> {
    private static final int PARAM_MATCH_STATE = 0;
    private static final int PARAM_MATCH_ROTATION = 1;
    private static final int PARAM_MATCH_POSITION = 2;

    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;

    public PositiveConditionMatchSnapshotExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        if (conditionItem.getWiredData().getParams().size() != 3) {
            return false;
        }

        boolean matchState = conditionItem.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        boolean matchRotation = conditionItem.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        boolean matchPosition = conditionItem.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        for (Long itemId : selectedItems) {
            RoomItemFloor floorItem = conditionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                conditionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            WiredItemSnapshot snapshot = conditionItem.getWiredData().getSnapshots().get(itemId);

            if (snapshot == null)
                continue;

            if (matchState && !floorItem.getExtraData().equals(snapshot.getExtraData())) {
                return false;
            }

            if (matchRotation && floorItem.getRotation() != snapshot.getRotation()) {
                return false;
            }

            if (matchPosition && floorItem.getPosition().getX() != snapshot.getX() && floorItem.getPosition().getY() != snapshot.getY()) {
                return false;
            }
        }

        return true;
    }
}
