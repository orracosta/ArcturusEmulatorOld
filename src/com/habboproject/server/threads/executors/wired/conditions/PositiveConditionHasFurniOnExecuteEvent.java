package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class PositiveConditionHasFurniOnExecuteEvent implements Callable<Boolean> {
    private final static int PARAM_MODE = 0;

    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;

    public PositiveConditionHasFurniOnExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        int mode;

        try {
            mode = conditionItem.getWiredData().getParams().get(PARAM_MODE);
        }
        catch (Exception e) {
            mode = 0;
        }

        int selectedItemsWithFurni = 0;
        for (Long itemId : selectedItems) {
            RoomItemFloor floorItem = conditionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                conditionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            for (RoomItemFloor itemOnSq : floorItem.getItemsOnStack()) {
                if (itemOnSq.getPosition().getZ() == 0.0 || itemOnSq.getPosition().getZ() <= floorItem.getPosition().getZ() || itemOnSq.getId() == floorItem.getId())
                    continue;

                ++selectedItemsWithFurni;
            }
        }

        boolean result = false;

        if (mode == 0) {
            if (selectedItemsWithFurni >= 1) {
                result = true;
            }
        } else if (selectedItemsWithFurni == selectedItems.size()) {
            result = true;
        }

        if (!result) return false;

        return true;
    }
}
