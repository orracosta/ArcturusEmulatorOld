package com.habboproject.server.threads.executors.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionBotTeleportExecuteEvent implements Callable<Boolean> {
    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionBotTeleportExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        if (selectedItems == null || selectedItems.isEmpty() || actionItem.getWiredData().getText().isEmpty()) {
            return false;
        }

        Long itemId = WiredUtil.getRandomElement(selectedItems);

        if (itemId == null) {
            return false;
        }

        RoomItemFloor item = actionItem.getRoom().getItems().getFloorItem(itemId.longValue());

        if (item == null) {
            actionItem.getWiredData().getSelectedIds().remove(itemId);
            return false;
        }

        if (item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
            return false;
        }

        String botName = actionItem.getWiredData().getText();

        BotEntity botEntity = actionItem.getRoom().getBots().getBotByName(botName);

        if (botEntity != null) {
            Position position = new Position(item.getPosition().getX(), item.getPosition().getY(), item.getTile().getWalkHeight());

            botEntity.applyEffect(new PlayerEffect(4, 5));
            botEntity.cancelWalk();
            botEntity.warp(position);

            return true;
        }

        return false;
    }
}
