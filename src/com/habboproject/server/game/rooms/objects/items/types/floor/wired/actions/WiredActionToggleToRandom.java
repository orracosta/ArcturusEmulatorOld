package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionToggleToRandomExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.List;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredActionToggleToRandom extends WiredActionItem {
    public WiredActionToggleToRandom(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 15;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        List<Position> tilesToUpdate = Lists.newArrayList();

        long itemId = WiredUtil.getRandomElement(this.getWiredData().getSelectedIds());

        final RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

        if (floorItem == null || floorItem instanceof WiredFloorItem)
            return;

        floorItem.onInteract(null, floorItem instanceof BanzaiTimerFloorItem || floorItem instanceof FootballTimerFloorItem || floorItem instanceof FreezeTimerFloorItem ? -2 : 0, true);

        tilesToUpdate.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY()));

        for (Position tileToUpdate : tilesToUpdate) {
            this.getRoom().getMapping().updateTile(tileToUpdate.getX(), tileToUpdate.getY());
        }

        tilesToUpdate.clear();
    }
}
