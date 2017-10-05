package com.habboproject.server.game.rooms.objects.items.types.floor.freeze;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GamePlayer;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeGateFloorItem extends RoomItemFloor {
    private GameTeam gameTeam;

    public FreezeGateFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        switch (this.getDefinition().getInteraction()) {
            case "freeze_blue_gate": {
                this.gameTeam = GameTeam.BLUE;
                break;
            }

            case "freeze_red_gate": {
                this.gameTeam = GameTeam.RED;
                break;
            }

            case "freeze_green_gate": {
                this.gameTeam = GameTeam.GREEN;
                break;
            }

            case "freeze_yellow_gate": {
                this.gameTeam = GameTeam.YELLOW;
                break;
            }

            default:
                break;
        }
    }

    public void onPlaced() {
        this.setExtraData("0");
        this.sendUpdate();
        this.save();
    }

    public void onPickup() {
        this.setExtraData("0");
        this.save();
    }

    public void onEntityStepOn(RoomEntity entity) {
        PlayerEntity playerEntity = null;
        if (!(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity)entity) == null || this.getTeam() == null || this.getTeam() == GameTeam.NONE) {
            return;
        }

        if (playerEntity.getMountedEntity() != null) {
            return;
        }

        if (playerEntity.getGameTeam() != null && playerEntity.getGameTeam() != GameTeam.NONE && playerEntity.getGameTeam() != this.getTeam()) {
            GameTeam oldTeam = playerEntity.getGameTeam();

            this.getRoom().getGame().removeFromTeam(oldTeam, playerEntity.getPlayerId());

            for (final RoomItemFloor floorItem : this.getRoom().getItems().getByInteraction("freeze_" + oldTeam.toString().toLowerCase() + "_gate")) {
                if (floorItem == null) {
                    continue;
                }

                floorItem.setExtraData(new StringBuilder().append(this.getRoom().getGame().getTeams().get(oldTeam).size()).toString());
                floorItem.sendUpdate();
            }
        }

        if (playerEntity.getGameTeam() != null && playerEntity.getGameTeam() == this.getTeam()) {
            this.getRoom().getGame().removeFromTeam(this.getTeam(), playerEntity.getPlayerId());

            playerEntity.setGameTeam(GameTeam.NONE);
            playerEntity.applyEffect(null);
        } else {
            this.getRoom().getGame().getTeams().get(this.getTeam()).add(new GamePlayer(playerEntity.getPlayerId()));

            playerEntity.setGameTeam(this.getTeam());
            playerEntity.applyEffect(new PlayerEffect(this.getTeam().getFreezeEffect(), 0));
        }

        this.updateTeamCount();
    }

    public void onEntityLeaveRoom(RoomEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.getGameTeam() == this.getTeam()) {
                this.getRoom().getGame().removeFromTeam(this.getTeam(), playerEntity.getPlayerId());
                this.updateTeamCount();
            }
        }
    }

    public boolean isMovementCancelled(RoomEntity entity) {
        return Integer.parseInt(this.getExtraData()) >= 5 || (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame);
    }

    public void updateTeamCount() {
        this.setExtraData(new StringBuilder().append(this.getRoom().getGame().getTeams().get(this.getTeam()).size()).toString());
        this.sendUpdate();
    }

    public GameTeam getTeam() {
        return this.gameTeam;
    }
}
