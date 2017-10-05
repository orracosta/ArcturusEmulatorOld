package com.habboproject.server.game.rooms.types.components.games.freeze;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.*;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameEnds;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerGameStarts;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GamePlayer;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.RoomGame;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.utilities.RandomInteger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeGame extends RoomGame {
    public FreezeGame(Room room) {
        super(room, GameType.FREEZE);
    }

    @Override
    public void onGameStarts() {
        WiredTriggerGameStarts.executeTriggers(this.room);
        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeTimerFloorItem.class)) {
            ((FreezeTimerFloorItem)floorItem).setRunning(true);
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeBlockFloorItem.class)) {
            floorItem.setExtraData("0");
            floorItem.sendUpdate();
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeExitTileFloorItem.class)) {
            floorItem.setExtraData("1");
            floorItem.sendUpdate();
        }

        this.updateScoreboard(null);
        this.getGameComponent().getGiveScoreTrigger().clear();

        this.countTeamPoint();

        loop : for (Iterator iterator = room.getEntities().getPlayerEntities().iterator(); iterator.hasNext();) {
            PlayerEntity playerEntity = (PlayerEntity) iterator.next();
            if (playerEntity != null) {
                if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) != GameTeam.NONE) {
                    for (RoomItemFloor floorItem : playerEntity.getRoom().getItems().getItemsOnSquare(playerEntity.getPosition().copy())) {
                        if (!(floorItem instanceof FreezeBlockFloorItem))
                            continue;

                        floorItem.setExtraData("1000");
                        floorItem.sendUpdate();
                    }

                    playerEntity.getPlayer().getFreeze().updateMyLife();
                    continue;
                }

                RoomItemFloor exitTile = this.getRandomExit();

                if (exitTile == null) continue;

                for (RoomItemFloor floorItem : this.room.getItems().getItemsOnSquare(playerEntity.getPosition().copy())) {
                    if (!(floorItem instanceof FreezeTileFloorItem))
                        continue;

                    playerEntity.warp(exitTile.getPosition().copy());
                    continue loop;
                }
            }
        }
    }

    @Override
    public void tick() {
        int value = this.gameLength - this.timer;
        if (value <= 0) {
            this.timer = this.gameLength;
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeTimerFloorItem.class)) {
            if (value < 0) {
                value = 0;
            }
            floorItem.setExtraData(String.valueOf(value));
            floorItem.sendUpdate();
        }

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (!entity.getEntityType().equals(RoomEntityType.PLAYER))
                continue;

            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) == GameTeam.NONE)
                continue;

            playerEntity.getPlayer().getFreeze().cycle();
        }
    }

    @Override
    public void onGameEnds() {
        GameTeam winningTeam = this.winningTeam();

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeTimerFloorItem.class)) {
            ((FreezeTimerFloorItem)floorItem).setRunning(false);
        }

        this.updateHighscoreItems();

        for (RoomEntity entity : this.room.getEntities().getAllEntities().values()) {
            if (!entity.getEntityType().equals(RoomEntityType.PLAYER))
                continue;

            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (this.getGameComponent().getTeam(playerEntity.getPlayerId()) == GameTeam.NONE)
                continue;

            if (this.getGameComponent().getTeam(playerEntity.getPlayerId()).equals(winningTeam) && winningTeam != GameTeam.NONE) {
                this.room.getEntities().broadcastMessage(new ActionMessageComposer(playerEntity.getId(), 1));
            }

            if (playerEntity.getPlayer().getFreeze().isFreezed()) {
                playerEntity.getPlayer().getFreeze().setFreezed(false);
                playerEntity.getPlayer().getFreeze().updateMyself();
            }

            playerEntity.getPlayer().getFreeze().reset();
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeExitTileFloorItem.class)) {
            floorItem.setExtraData("0");
            floorItem.sendUpdate();
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeBlockFloorItem.class)) {
            if (floorItem.getExtraData().equals("0") || floorItem.getExtraData().isEmpty())
                continue;

            floorItem.setExtraData("1000");
            floorItem.sendUpdate();
        }

        for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeGateFloorItem.class)) {
            if (floorItem.getExtraData().equals("0") || floorItem.getExtraData().isEmpty())
                continue;

            ((FreezeGateFloorItem)floorItem).updateTeamCount();
        }

        this.getGameComponent().resetScores();

        WiredTriggerGameEnds.executeTriggers(this.room);
    }

    public void countTeamPoint() {
        for (Map.Entry<GameTeam, List<GamePlayer>> team : this.getGameComponent().getTeams().entrySet()) {
            if (team.getValue().size() <= 0)
                continue;

            this.getGameComponent().increaseTeamScore(team.getKey(), team.getValue().size() * 40);
        }
    }

    public void playerDies(PlayerEntity playerEntity) {
        ThreadManager.getInstance().executeSchedule(new CometThread() {
            @Override
            public void run() {
                playerEntity.applyEffect(new PlayerEffect(0, 0));
                playerEntity.setGameTeam(GameTeam.NONE);
                playerEntity.getPlayer().getFreeze().reset();
            }
        }, 500, TimeUnit.MILLISECONDS);

        RoomItemFloor exitTile = this.getRandomExit();
        if (exitTile != null) {
            playerEntity.warp(exitTile.getPosition().copy());
        }

        int emptyTeams = 0;
        for (Map.Entry<GameTeam, List<GamePlayer>> team : this.getGameComponent().getTeams().entrySet()) {
            if (team.getValue().size() > 0)
                continue;

            ++emptyTeams;
        }

        if (emptyTeams >= 3) {
            for (RoomItemFloor floorItem : this.room.getItems().getByClass(FreezeTimerFloorItem.class)) {
                floorItem.setExtraData("0");
                floorItem.sendUpdate();
            }

            this.onGameEnds();
            this.room.getGame().stop();
        }
    }

    public RoomItemFloor getRandomExit() {
        List<FreezeExitTileFloorItem> exitTiles = this.room.getItems().getByClass(FreezeExitTileFloorItem.class);

        if (exitTiles == null || exitTiles.size() <= 0) {
            return null;
        }

        return exitTiles.get(RandomInteger.getRandom(0, exitTiles.size() - 1));
    }
}
