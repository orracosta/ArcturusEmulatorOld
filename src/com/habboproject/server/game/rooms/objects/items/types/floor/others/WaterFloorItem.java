package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;

import java.util.List;
import java.util.Map;

/**
 * Created by brend on 04/02/2017.
 */
public class WaterFloorItem extends RoomItemFloor {
    private boolean posA = false;
    private boolean posB = false;
    private boolean posC = false;
    private boolean posD = false;
    private boolean posE = false;
    private boolean posF = false;
    private boolean posG = false;
    private boolean posH = false;
    private boolean posI = false;
    private boolean posJ = false;
    private boolean posK = false;
    private boolean posL = false;

    public WaterFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    public void refresh() {
        for (RoomItemFloor floorItem : this.getRoom().getItems().getByClass(WaterFloorItem.class)) {
            ((WaterFloorItem)floorItem).calculateExtraData();
        }
    }

    public void calculateExtraData() {
        int extraData = 0;

        Map<Integer, List<Integer>> tiles = this.getRoom().getItems().getWaterTiles();

        if (checkPosition(tiles, -1, -1)) {
            extraData += getValue(1);
        }

        if (checkPosition(tiles, 0, -1)) {
            extraData += getValue(2);
        }

        if (checkPosition(tiles, +1, -1)) {
            extraData += getValue(3);
        }

        if (checkPosition(tiles, +2, -1)) {
            extraData += getValue(4);
        }

        if (checkPosition(tiles, -1, 0)) {
            extraData += getValue(5);
        }

        if (checkPosition(tiles, +2, 0)) {
            extraData += getValue(6);
        }

        if (checkPosition(tiles, -1, +1)) {
            extraData += getValue(7);
        }

        if (checkPosition(tiles, +2, +1)) {
            extraData += getValue(8);
        }

        if (checkPosition(tiles, -1, +2)) {
            extraData += getValue(9);
        }

        if (checkPosition(tiles, 0, +2)) {
            extraData += getValue(10);
        }

        if (checkPosition(tiles, +1, +2)) {
            extraData += getValue(11);
        }

        if (checkPosition(tiles, +2, +2)) {
            extraData += getValue(12);
        }

        if (!posB && checkTileBlocked(0, -1)) {
            extraData += getValue(2);
        }

        if (!posC && checkTileBlocked(+1, -1)) {
            extraData += getValue(3);
        }

        if (!posE && checkTileBlocked(-1, 0)) {
            extraData += getValue(5);
        }

        if (!posF && checkTileBlocked(+2, 0)) {
            extraData += getValue(6);
        }

        if (!posG && checkTileBlocked(-1, +1)) {
            extraData += getValue(7);
        }

        if (!posH && checkTileBlocked(+2, +1)) {
            extraData += getValue(8);
        }

        if (!posJ && checkTileBlocked(0, +2)) {
            extraData += getValue(10);
        }

        if (!posK && checkTileBlocked(+1, +2)) {
            extraData += getValue(11);
        }

        this.setExtraData("" + extraData);
        this.sendUpdate();

        this.save();

        this.reset();
    }

    private boolean checkPosition(Map<Integer, List<Integer>> tiles, int diffX, int diffY) {
        return tiles.containsKey(this.getPosition().getX() + diffX) && tiles.get(this.getPosition().getX() + diffX).contains(this.getPosition().getY() + diffY);
    }

    private boolean checkTileBlocked(int diffX, int diffY) {
        return !this.getRoom().getMapping().isValidPosition(new Position(this.getPosition().getX() + diffX, this.getPosition().getY() + diffY));
    }

    private int getValue(int pos) {
        int value = 0;
        switch (pos) {
            case 1:
                if (!posA) {
                    value = 2048;
                    posA = true;
                }
                break;

            case 2:
                if (!posB) {
                    value = 1024;
                    posB = true;
                }
                break;

            case 3:
                if (!posC) {
                    value = 512;
                    posC = true;
                }
                break;

            case 4:
                if (!posD) {
                    value = 256;
                    posD = true;
                }
                break;

            case 5:
                if (!posE) {
                    value = 128;
                    posE = true;
                }
                break;

            case 6:
                if (!posF) {
                    value = 64;
                    posF = true;
                }
                break;

            case 7:
                if (!posG) {
                    value = 32;
                    posG = true;
                }
                break;

            case 8:
                if (!posH) {
                    value = 16;
                    posH = true;
                }
                break;

            case 9:
                if (!posI) {
                    value = 8;
                    posI = true;
                }
                break;

            case 10:
                if (!posJ) {
                    value = 4;
                    posJ = true;
                }
                break;

            case 11:
                if (!posK) {
                    value = 2;
                    posK = true;
                }
                break;

            case 12:
                if (!posL) {
                    value = 1;
                    posL = true;
                }
                break;

            default:
                break;
        }

        return value;
    }

    private void reset() {
        this.posA = false;
        this.posB = false;
        this.posC = false;
        this.posD = false;
        this.posE = false;
        this.posF = false;
        this.posG = false;
        this.posH = false;
        this.posI = false;
        this.posJ = false;
        this.posK = false;
        this.posL = false;
    }
}
