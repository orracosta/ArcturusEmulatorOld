package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class NegativeConditionTriggererOnFurniExecuteEvent implements Callable<Boolean> {
    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;
    private final RoomEntity entity;

    public NegativeConditionTriggererOnFurniExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems, RoomEntity entity) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
        this.entity = entity;
    }

    @Override
    public Boolean call() throws Exception {
        if (entity == null) {
            return false;
        }

        for (Long itemId : selectedItems) {
            for (RoomItemFloor itemOnEntity : entity.getRoom().getItems().getItemsOnSquare(entity.getPosition().getX(), entity.getPosition().getY())) {
                if (itemOnEntity.getId() != itemId)
                    continue;

                return false;
            }
        }

        return true;
    }
}
