package com.habboproject.server.game.rooms.types.components.games.football;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballScoreFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameEnds;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameStarts;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.RoomGame;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;

/**
 * Created by brend on 04/02/2017.
 */
public class FootballGame extends RoomGame {
    public FootballGame(Room room) {
        super(room, GameType.FOOTBALL);
    }

    @Override
    public void tick() {
        int value = this.gameLength - this.timer;
        if (value <= 0) {
            this.timer = this.gameLength;
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FootballTimerFloorItem.class)) {
            if (value < 0) {
                value = 0;
            }

            floorItem.setExtraData(String.valueOf(value));
            floorItem.sendUpdate();
        }
    }

    @Override
    public void onGameEnds() {
        GameTeam winningTeam = this.winningTeam();

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FootballTimerFloorItem.class)) {
            ((FootballTimerFloorItem)floorItem).setRunning(false);
        }

        this.updateHighscoreItems();

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (!entity.getEntityType().equals(RoomEntityType.PLAYER))
                continue;

            PlayerEntity playerEntity = (PlayerEntity)entity;

            if (!this.getGameComponent().getTeam(playerEntity.getPlayerId()).equals((Object)winningTeam) || winningTeam == GameTeam.NONE)
                continue;

            this.room.getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), 1));
        }

        this.getGameComponent().resetScores();

        WiredTriggerGameEnds.executeTriggers(this.room);
    }

    @Override
    public void onGameStarts() {
        WiredTriggerGameStarts.executeTriggers(this.room);
        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FootballTimerFloorItem.class)) {
            ((FootballTimerFloorItem)floorItem).setRunning(true);
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FootballScoreFloorItem.class)) {
            ((FootballScoreFloorItem)floorItem).reset();
        }

        this.getGameComponent().getGiveScoreTrigger().clear();
    }
}
