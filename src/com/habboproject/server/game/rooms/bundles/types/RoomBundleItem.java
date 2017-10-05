package com.habboproject.server.game.rooms.bundles.types;

public class RoomBundleItem {
    private int itemId;

    private int x;
    private int y;
    private double z;
    private int rotation;

    private String wallPosition;

    private String extraData;

    public RoomBundleItem(int itemId, int x, int y, double z, int rotation, String wallPosition, String extraData) {
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.extraData = extraData;
        this.wallPosition = wallPosition;
    }

    public int getItemId() {
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

    public void setZ(int z) {
        this.z = z;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getWallPosition() {
        return wallPosition;
    }

    public int getRotation() {
        return rotation;
    }
}
