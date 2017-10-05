package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class PositiveConditionFurniHasPlayersExecuteEvent implements Callable<Boolean> {
    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;

    public PositiveConditionFurniHasPlayersExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        for (Long itemId : selectedItems) {
            RoomItemFloor floorItem = conditionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                conditionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            List<RoomEntity> entitiesOnItem = floorItem.getEntitiesOnItem();

            if (entitiesOnItem == null || !(entitiesOnItem.size() > 0))
                return false;
        }

        return true;
    }
}
