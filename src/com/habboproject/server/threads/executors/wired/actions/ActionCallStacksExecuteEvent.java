package com.habboproject.server.threads.executors.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionCallStacks;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionCallStacksExecuteEvent implements Callable<Boolean> {
    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;
    private final RoomEntity entity;
    private final Object data;

    public ActionCallStacksExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems, RoomEntity entity, Object data) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
        this.entity = entity;
        this.data = data;
    }

    @Override
    public Boolean call() throws Exception {
        List<Position> tilesToExecute = Lists.newArrayList();

        for (long itemId : actionItem.getWiredData().getSelectedIds()) {
            final RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null || (floorItem.getPosition().getX() == actionItem.getPosition().getX() && floorItem.getPosition().getY() == actionItem.getPosition().getY()))
                continue;

            tilesToExecute.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY()));
        }

        for (Position tileToUpdate : tilesToExecute) {
            for(RoomItemFloor roomItemFloor : actionItem.getRoom().getMapping().getTile(tileToUpdate).getItems()) {
                if(roomItemFloor instanceof WiredActionItem) {
                    ((WiredActionItem) roomItemFloor).evaluate(entity, data);
                }
            }
        }

        tilesToExecute.clear();
        return true;
    }
}
