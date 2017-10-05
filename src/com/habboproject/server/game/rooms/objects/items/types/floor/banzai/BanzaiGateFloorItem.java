package com.habboproject.server.game.rooms.objects.items.types.floor.banzai;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GamePlayer;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.banzai.BanzaiGame;


public class BanzaiGateFloorItem extends DefaultFloorItem {
    private GameTeam gameTeam;

    public BanzaiGateFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        switch (this.getDefinition().getInteraction()) {
            case "bb_red_gate":
                gameTeam = GameTeam.RED;
                break;
            case "bb_blue_gate":
                gameTeam = GameTeam.BLUE;
                break;
            case "bb_green_gate":
                gameTeam = GameTeam.GREEN;
                break;
            case "bb_yellow_gate":
                gameTeam = GameTeam.YELLOW;
                break;
        }
    }

    @Override
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

            for (RoomItemFloor floorItem : this.getRoom().getItems().getByInteraction("bb_" + oldTeam.toString().toLowerCase() + "_gate")) {
                if (floorItem == null)
                    continue;

                floorItem.setExtraData("" + this.getRoom().getGame().getTeams().get((Object)oldTeam).size());
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
            playerEntity.applyEffect(new PlayerEffect(this.getTeam().getBanzaiEffect(), 0));
        }

        this.updateTeamCount();
    }

    @Override
    public void onEntityLeaveRoom(RoomEntity entity) {
        PlayerEntity playerEntity;
        if (entity instanceof PlayerEntity && (playerEntity = (PlayerEntity)entity).getGameTeam() == this.getTeam()) {
            this.getRoom().getGame().removeFromTeam(this.getTeam(), playerEntity.getPlayerId());
            this.updateTeamCount();
        }
    }

    @Override
    public boolean isMovementCancelled(RoomEntity entity) {
        if (Integer.parseInt(this.getExtraData()) >= 5) {
            return true;
        }

        if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof BanzaiGame) {
            return true;
        }

        return false;
    }

    private void updateTeamCount() {
        this.setExtraData("" + this.getRoom().getGame().getTeams().get(this.getTeam()).size());
        this.sendUpdate();
    }

    public GameTeam getTeam() {
        return this.gameTeam;
    }
}
