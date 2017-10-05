package com.habboproject.server.game.rooms.types.components.games.banzai;

import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiSphereFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTileFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameEnds;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameStarts;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.RoomGame;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;


public class BanzaiGame extends RoomGame {
    private int startBanzaiTileCount = 0;
    private int banzaiTileCount = 0;

    public BanzaiGame(Room room) {
        super(room, GameType.BANZAI);
    }

    @Override
    public void tick() {
        if (this.startBanzaiTileCount != 0 && this.banzaiTileCount == 0) {
            this.timer = this.gameLength;
        }

        int value = this.gameLength - this.timer;
        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiTimerFloorItem.class)) {
            if (value < 0) {
                value = 0;
            }

            item.setExtraData(String.valueOf(value));
            item.sendUpdate();
        }

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (!entity.getEntityType().equals(RoomEntityType.PLAYER))
                continue;

            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) == GameTeam.NONE)
                continue;

            if (playerEntity.getBanzaiPlayerAchievement() >= 60) {
                playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.BB_PLAYER, 1);
                playerEntity.setBanzaiPlayerAchievement(0);
                continue;
            }

            playerEntity.incrementBanzaiPlayerAchievement();
        }
    }

    @Override
    public void onGameStarts() {
        WiredTriggerGameStarts.executeTriggers(this.room);

        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiTimerFloorItem.class)) {
            ((BanzaiTimerFloorItem) item).setRunning(true);
        }

        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiSphereFloorItem.class)) {
            item.setExtraData("1");
            item.sendUpdate();
        }

        this.banzaiTileCount = 0;
        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiTileFloorItem.class)) {
            ((BanzaiTileFloorItem) item).onGameStarts();
            ++this.banzaiTileCount;
        }

        this.startBanzaiTileCount = this.banzaiTileCount;
        this.countDown = 3;

        this.updateScoreboard(null);
        this.getGameComponent().getGiveScoreTrigger().clear();
    }

    @Override
    public void onGameEnds() {
        GameTeam winningTeam = this.winningTeam();

        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiTimerFloorItem.class)) {
            ((BanzaiTimerFloorItem) item).setRunning(false);
            item.sendUpdate();
        }

        for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiTileFloorItem.class)) {
            if (((BanzaiTileFloorItem) item).getTeam() == winningTeam && winningTeam != GameTeam.NONE) {
                ((BanzaiTileFloorItem) item).flash();
                continue;
            }

            ((BanzaiTileFloorItem) item).onGameEnds();
        }

        this.updateHighscoreItems();

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (!entity.getEntityType().equals(RoomEntityType.PLAYER))
                continue;

            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (!this.getGameComponent().getTeam(playerEntity.getPlayerId()).equals(winningTeam) || winningTeam == GameTeam.NONE)
                continue;

            playerEntity.getPlayer().getAchievements().progressAchievement(AchievementType.BB_WINNER, 1);
            this.room.getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), 1));
        }

        this.getGameComponent().resetScores();

        WiredTriggerGameEnds.executeTriggers(this.room);
    }

    public void increaseScoreByPlayer(GameTeam team, PlayerEntity playerEntity, int amount) {
        this.getGameComponent().increasePlayerScore(team, playerEntity, amount);
        this.updateScoreboard(team);
    }

    public void addTile() {
        this.banzaiTileCount += 1;
        this.startBanzaiTileCount += 1;
    }

    public void removeTile() {
        this.banzaiTileCount -= 1;
        this.startBanzaiTileCount -= 1;
    }

    public void decreaseTileCount() {
        --this.banzaiTileCount;
    }
}
