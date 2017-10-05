package com.habboproject.server.threads.executors.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.threads.CometThread;

import java.util.List;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionToggleToRandomExecuteEvent implements CometThread {
    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionToggleToRandomExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        List<Position> tilesToUpdate = Lists.newArrayList();

        long itemId = WiredUtil.getRandomElement(selectedItems);

        RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId);

        if (floorItem == null || floorItem instanceof WiredFloorItem /*|| floorItem instanceof DiceFloorItem*/) {
            actionItem.getWiredData().getSelectedIds().remove(itemId);
            return;
        }

        floorItem.onInteract(null, floorItem instanceof BanzaiTimerFloorItem || floorItem instanceof FootballTimerFloorItem || floorItem instanceof FreezeTimerFloorItem ? -2 : 0, true);

        tilesToUpdate.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY()));

        for (Position tileToUpdate : tilesToUpdate) {
            actionItem.getRoom().getMapping().updateTile(tileToUpdate.getX(), tileToUpdate.getY());
        }

        tilesToUpdate.clear();
    }
}
