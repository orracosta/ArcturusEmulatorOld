package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class PositiveConditionTriggererOnFurniExecuteEvent implements Callable<Boolean> {
    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;
    private final RoomEntity entity;

    public PositiveConditionTriggererOnFurniExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems, RoomEntity entity) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
        this.entity = entity;
    }

    @Override
    public Boolean call() throws Exception {
        if (entity == null) {
            return false;
        }

        boolean itsOkay = false;
        for (Long itemId : selectedItems) {
            for (RoomItemFloor itemOnEntity : entity.getRoom().getItems().getItemsOnSquare(entity.getPosition().getX(), entity.getPosition().getY())) {
                if (itemOnEntity.getId() != itemId)
                    continue;

                itsOkay = true;
                break;
            }
        }

        if (!itsOkay) return false;

        return true;
    }
}
