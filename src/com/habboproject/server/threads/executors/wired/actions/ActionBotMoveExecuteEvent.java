package com.habboproject.server.threads.executors.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionBotMoveExecuteEvent implements Callable<Boolean> {
    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionBotMoveExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return false;
        }

        Long itemId = WiredUtil.getRandomElement(selectedItems);

        if (itemId == null) {
            return false;
        }

        String botName = actionItem.getWiredData().getText();
        BotEntity botEntity = actionItem.getRoom().getBots().getBotByName(botName);

        if (botEntity != null) {
            RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId.longValue());

            if (floorItem == null) {
                actionItem.getWiredData().getSelectedIds().remove(itemId);
                return false;
            }

            if (botEntity.isWalking()) {
                botEntity.cancelWalk();
            }

            if (floorItem.getPosition().getX() != actionItem.getRoom().getModel().getDoorX() && floorItem.getPosition().getY() != actionItem.getRoom().getModel().getDoorY()) {
                botEntity.getData().setForcedFurniTargetMovement(itemId.longValue());
                botEntity.moveTo(floorItem.getPosition().getX(), floorItem.getPosition().getY());
            }

            return true;
        }

        return false;
    }
}
