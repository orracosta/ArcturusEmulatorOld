package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

public class WiredActionTeleportPlayer extends WiredActionItem {
    public WiredActionTeleportPlayer(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null) return;

        if (this.getWiredData() == null || this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().isEmpty()) {
            event.entity = null;
            return;
        }

        Long itemId = WiredUtil.getRandomElement(this.getWiredData().getSelectedIds());

        if (itemId == null) {
            event.entity = null;
            return;
        }

        RoomItemFloor item = this.getRoom().getItems().getFloorItem(itemId);

        if (item == null || item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
            event.entity = null;
            return;
        }

        Position position = new Position(item.getPosition().getX(), item.getPosition().getY(), item.getTile().getWalkHeight());

        event.entity.applyEffect(new PlayerEffect(4, 5));

        event.entity.cancelWalk();
        event.entity.warp(position);

        event.entity = null;
    }


    @Override
    public int getInterface() {
        return 0;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
