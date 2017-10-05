package com.habboproject.server.threads.executors.wired.conditions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class NegativeConditionStuffIsExecuteEvent implements Callable<Boolean> {
    private final WiredConditionItem conditionItem;
    private final List<Long> selectedItems;
    private final RoomEntity entity;
    private final Object data;

    public NegativeConditionStuffIsExecuteEvent(WiredConditionItem conditionItem, List<Long> selectedItems, RoomEntity entity, Object data) {
        this.conditionItem = conditionItem;
        this.selectedItems = selectedItems;
        this.entity = entity;
        this.data = data;
    }

    @Override
    public Boolean call() throws Exception {
        RoomItemFloor item = null;

        if (data == null || !(data instanceof RoomItemFloor)) {
            List<RoomItemFloor> floorItems;
            if (entity != null && entity instanceof PlayerEntity && (floorItems = conditionItem.getRoom().getItems().getItemsOnSquare(entity.getPosition().getX(), entity.getPosition().getY())) != null && floorItems.size() > 0) {
                item = floorItems.get(0);
            }
        } else {
            item = (RoomItemFloor)data;
        }

        if (item == null) {
            return false;
        }

        if (selectedItems == null || selectedItems.isEmpty()) {
            return false;
        }

        for (Long itemId : selectedItems) {
            RoomItemFloor floorItem = conditionItem.getRoom().getItems().getFloorItem(itemId);
            if (floorItem == null)
                continue;

            if (item.getDefinition().getItemName() == floorItem.getDefinition().getItemName() || item.getDefinition().getInteraction() == floorItem.getDefinition().getInteraction()) {
               return false;
            }
        }

        return true;
    }
}
