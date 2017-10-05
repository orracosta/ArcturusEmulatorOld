package com.habboproject.server.game.rooms.objects.items.types.floor.wired;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;


public class WiredItemSnapshot {
    private long itemId;
    private int x;
    private int y;
    private double z;
    private int rotation;
    private String extraData;

    public WiredItemSnapshot(RoomItemFloor floorItem) {
        this.itemId = floorItem.getId();
        this.x = floorItem.getPosition().getX();
        this.y = floorItem.getPosition().getY();
        this.z = floorItem.getPosition().getZ();
        this.rotation = floorItem.getRotation();
        this.extraData = floorItem.getExtraData();
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public interface Refreshable {
        public void refreshSnapshots();
    }
}
