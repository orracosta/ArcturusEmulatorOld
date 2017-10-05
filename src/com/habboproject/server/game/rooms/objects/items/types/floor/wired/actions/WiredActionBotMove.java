package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionBotMoveExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.concurrent.ExecutionException;

public class WiredActionBotMove extends WiredActionItem {
    public WiredActionBotMove(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData() == null || this.getWiredData().getSelectedIds() == null || this.getWiredData().getSelectedIds().isEmpty()) {
            return;
        }

        Long itemId = WiredUtil.getRandomElement(this.getWiredData().getSelectedIds());

        if (itemId == null) {
            return;
        }

        RoomItemFloor item = this.getRoom().getItems().getFloorItem(itemId);

        if (item == null || item.isAtDoor() || item.getPosition() == null || item.getTile() == null) {
            this.getWiredData().getSelectedIds().remove(itemId);
            return;
        }

        Position position = new Position(item.getPosition().getX(), item.getPosition().getY());

        final String entityName = this.getWiredData().getText();

        final BotEntity botEntity = this.getRoom().getBots().getBotByName(entityName);

        if(botEntity == null) {
            return;
        }

        if (position.getX() != this.getRoom().getModel().getDoorX() && position.getY() != this.getRoom().getModel().getDoorY()) {
            botEntity.getData().setForcedFurniTargetMovement(itemId.longValue());
            botEntity.moveTo(position);
        }
    }

    @Override
    public int getInterface() {
        return 22;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
