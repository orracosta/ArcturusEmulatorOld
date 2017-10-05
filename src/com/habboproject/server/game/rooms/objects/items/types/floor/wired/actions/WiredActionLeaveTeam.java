package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.List;


public class WiredActionLeaveTeam extends WiredActionItem {
    public WiredActionLeaveTeam(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 10;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) event.entity;
        if (playerEntity.getGameTeam() == null) {
            return;
        }

        if (playerEntity.getCurrentEffect() == null) {
            return;
        }

        boolean isFreeze = (playerEntity.getCurrentEffect().getEffectId() == playerEntity.getGameTeam().getFreezeEffect());

        if (isFreeze) {
            GameTeam oldTeam = playerEntity.getGameTeam();
            playerEntity.applyEffect(new PlayerEffect(0, 0));

            getRoom().getGame().removeFromTeam(playerEntity.getGameTeam(), playerEntity.getPlayerId());
            playerEntity.setGameTeam(null);

            List<FreezeGateFloorItem> gameGates = this.getRoom().getItems().getByClass(FreezeGateFloorItem.class);
            if (gameGates != null && gameGates.size() > 0) {
                for (RoomItemFloor floorItem : gameGates) {
                    if (floorItem == null) {
                        continue;
                    }

                    if (!((BanzaiGateFloorItem) floorItem).getTeam().equals(oldTeam)) {
                        continue;
                    }

                    floorItem.setExtraData(new StringBuilder().append(this.getRoom().getGame().getTeams().get(oldTeam).size()).toString());
                    floorItem.sendUpdate();
                }
            }
        }
    }
}
