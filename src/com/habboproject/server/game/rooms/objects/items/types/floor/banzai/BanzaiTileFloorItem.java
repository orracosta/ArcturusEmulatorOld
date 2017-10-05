package com.habboproject.server.game.rooms.objects.items.types.floor.banzai;

import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.banzai.BanzaiGame;

import java.util.LinkedList;
import java.util.List;

public class BanzaiTileFloorItem extends RoomItemFloor {
    private GameTeam gameTeam = GameTeam.NONE;
    private int points = 0;

    public BanzaiTileFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.setExtraData("0");
    }

    @Override
    public void onPickup() {
        if(!(this.getRoom().getGame().getInstance() instanceof BanzaiGame)) {
            return;
        }

        ((BanzaiGame) this.getRoom().getGame().getInstance()).removeTile();
    }

    @Override
    public void onPlaced() {
        if(!(this.getRoom().getGame().getInstance() instanceof BanzaiGame)) {
            return;
        }

        ((BanzaiGame) this.getRoom().getGame().getInstance()).addTile();
    }

    @Override
    public void onEntityPostStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).getGameTeam() == GameTeam.NONE || !(this.getRoom().getGame().getInstance() instanceof BanzaiGame)) {
            return;
        }

        if (this.points == 3) {
            // It's locked, what you doing?!?
            return;
        }

        if (((PlayerEntity) entity).getGameTeam() == this.gameTeam) {
            this.points++;
        } else {
            this.gameTeam = ((PlayerEntity) entity).getGameTeam();
            this.points = 1;
        }

        if (this.points == 3) {
            ((PlayerEntity) entity).getPlayer().getAchievements().progressAchievement(AchievementType.BB_TILES_LOCKED, 1);
            ((BanzaiGame) this.getRoom().getGame().getInstance()).increaseScoreByPlayer(gameTeam, ((PlayerEntity) entity), 1);
            ((BanzaiGame) this.getRoom().getGame().getInstance()).decreaseTileCount();

            final List<BanzaiTileFloorItem> rectangle = buildBanzaiRectangle(this, this.getPosition().getX(), this.getPosition().getY(), 0, 0, -1, 4, gameTeam);

            if (rectangle != null) {
                for (RoomItemFloor floorItem : this.getRoom().getItems().getByClass(BanzaiTileFloorItem.class)) {
                    BanzaiTileFloorItem tileItem = ((BanzaiTileFloorItem) floorItem);
                    if (tileItem.getPoints() == 3) continue;

                    final boolean[] borderCheck = new boolean[4];

                    for (BanzaiTileFloorItem rectangleItem : rectangle) {
                        if (rectangleItem.getPosition().getY() == floorItem.getPosition().getY()) {
                            if (rectangleItem.getPosition().getX() > floorItem.getPosition().getX()) {
                                borderCheck[0] = true;
                            } else {
                                borderCheck[1] = true;
                            }
                        } else if (rectangleItem.getPosition().getX() == floorItem.getPosition().getX()) {
                            if (rectangleItem.getPosition().getY() > floorItem.getPosition().getY()) {
                                borderCheck[2] = true;
                            } else {
                                borderCheck[3] = true;
                            }
                        }
                    }

                    if (borderCheck[0] && borderCheck[1] && borderCheck[2] && borderCheck[3]) {
                        if (tileItem.getId() != this.getId()) {
                            tileItem.setPoints(3);
                            tileItem.setTeam(this.gameTeam);

                            ((PlayerEntity) entity).getPlayer().getAchievements().progressAchievement(AchievementType.BB_TILES_LOCKED, 1);
                            ((BanzaiGame) this.getRoom().getGame().getInstance()).increaseScoreByPlayer(gameTeam, ((PlayerEntity) entity), 1);
                            ((BanzaiGame) this.getRoom().getGame().getInstance()).decreaseTileCount();
                            tileItem.updateTileData();
                        }
                    }
                }
            }
        }

        this.updateTileData();
    }

    @Override
    public void onItemAddedToStack(RoomItemFloor roomItemFloor) {
        if (roomItemFloor instanceof BanzaiPuckFloorItem) {
            if (((BanzaiPuckFloorItem) roomItemFloor).getPusher() != null) {
                this.onEntityPostStepOn(((BanzaiPuckFloorItem) roomItemFloor).getPusher());
            }
        }
    }

    private static List<BanzaiTileFloorItem> buildBanzaiRectangle(final BanzaiTileFloorItem triggerItem, final int x, final int y,
                                                                  final int goX, final int goY, final int currentDirection, final int turns, final GameTeam team) {
        final boolean[] directions = new boolean[4];

        if (goX == -1 || goX == 0) {
            directions[0] = true;
        }
        if (goX == 1 || goX == 0) {
            directions[2] = true;
        }
        if (goY == -1 || goY == 0) {
            directions[1] = true;
        }
        if (goY == 1 || goY == 0) {
            directions[3] = true;
        }

        if ((goX != 0 || goY != 0) && triggerItem.getPosition().getX() == x && triggerItem.getPosition().getY() == y) {
            return new LinkedList<>();
        }

        final Room room = triggerItem.getRoom();

        for (int i = 0; i < 4; ++i) {
            if (!directions[i]) {
                continue;
            }

            int nextXStep = 0, nextYStep = 0;

            if (i == 0 || i == 2) {
                nextXStep = (i == 0) ? 1 : -1;
            } else if (i == 1 || i == 3) {
                nextYStep = (i == 1) ? 1 : -1;
            }

            final int nextX = x + nextXStep;
            final int nextY = y + nextYStep;

            if (room.getMapping().getTile(nextX, nextY) != null) {
                RoomItemFloor obj = room.getItems().getFloorItem(room.getMapping().getTile(nextX, nextY).getTopItem());

                if (obj != null && obj instanceof BanzaiTileFloorItem) {
                    final BanzaiTileFloorItem item = (BanzaiTileFloorItem) obj;

                    if (item.getTeam() == team && item.getPoints() == 3) {
                        List<BanzaiTileFloorItem> foundPatches = null;
                        if (currentDirection != i && currentDirection != -1) {
                            if (turns > 0) {
                                foundPatches = buildBanzaiRectangle(
                                        triggerItem, nextX, nextY,
                                        (nextXStep == 0) ? (goX * -1) : (nextXStep * -1),
                                        (nextYStep == 0) ? (goY * -1) : (nextYStep * -1),
                                        i, (turns - 1), team
                                );
                            }
                        } else {
                            foundPatches = buildBanzaiRectangle(
                                    triggerItem, nextX, nextY,
                                    (nextXStep == 0) ? goX : (nextXStep * -1),
                                    (nextYStep == 0) ? goY : (nextYStep * -1),
                                    i, turns, team
                            );
                        }
                        if (foundPatches != null) {
                            foundPatches.add(item);
                            return foundPatches;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean needsChange = false;
    private int ticker = 0;

    @Override
    public void onTick() {
        if (this.hasTicks() && this.ticker >= RoomItemFactory.getProcessTime(0.5)) {
            if (needsChange) {
                this.setExtraData("1");
                this.sendUpdate();
                this.needsChange = false;
            } else {
                this.needsChange = true;
                this.updateTileData();
            }

            this.ticker = 0;
        }

        this.ticker++;
    }

    @Override
    public void onTickComplete() {
        this.updateTileData();
    }

    public void flash() {
        if (this.points == 3) {
            this.needsChange = true;
            this.setTicks(RoomItemFactory.getProcessTime(3.5));//3.5s
        }
    }

    public void onGameStarts() {
        this.gameTeam = GameTeam.NONE;
        this.points = 0;
        this.updateTileData();
    }

    public void onGameEnds() {
        this.setExtraData("0");
        this.sendUpdate();
    }

    public void updateTileData() {
        if (this.points != 0)
            this.setExtraData(((this.points + (gameTeam.getTeamId() * 3) - 1) + ""));
        else
            this.setExtraData("1");
        this.sendUpdate();
    }

    public GameTeam getTeam() {
        return this.gameTeam;
    }

    public void setTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
