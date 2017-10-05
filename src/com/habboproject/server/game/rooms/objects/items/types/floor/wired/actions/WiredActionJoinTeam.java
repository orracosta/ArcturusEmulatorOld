package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GamePlayer;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.List;

public class WiredActionJoinTeam extends WiredActionItem {
    private static final int PARAM_TEAM_ID = 0;

    public WiredActionJoinTeam(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() != 1) {
            this.getWiredData().getParams().put(PARAM_TEAM_ID, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 9;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (event.entity == null || !(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) event.entity;
        if (playerEntity.getGameTeam() != GameTeam.NONE && playerEntity.getGameTeam() == this.getTeam()) {
            return;
        }

        playerEntity.setGameTeam(this.getTeam());
        getRoom().getGame().getTeams().get(this.getTeam()).add(new GamePlayer(playerEntity.getPlayerId()));

        playerEntity.applyEffect(new PlayerEffect(this.getTeam().getFreezeEffect(), false));

        List<FreezeGateFloorItem> gameGates = this.getRoom().getItems().getByClass(FreezeGateFloorItem.class);
        if (gameGates != null && gameGates.size() > 0) {
            for (RoomItemFloor floorItem : gameGates) {
                if (floorItem == null) {
                    continue;
                }

                if (!((BanzaiGateFloorItem) floorItem).getTeam().equals(this.getTeam())) {
                    continue;
                }

                floorItem.setExtraData(new StringBuilder().append(this.getRoom().getGame().getTeams().get(this.getTeam()).size()).toString());
                floorItem.sendUpdate();
            }
        }
    }

    private GameTeam getTeam() {
        switch (this.getWiredData().getParams().get(0)) {
            case 1: {
                return GameTeam.RED;
            }

            case 2: {
                return GameTeam.GREEN;
            }

            case 3: {
                return GameTeam.BLUE;
            }

            case 4: {
                return GameTeam.YELLOW;
            }

            default: {
                return GameTeam.NONE;
            }
        }
    }
}
