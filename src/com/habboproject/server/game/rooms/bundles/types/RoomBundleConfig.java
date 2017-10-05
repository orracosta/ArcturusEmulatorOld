package com.habboproject.server.game.rooms.bundles.types;

public class RoomBundleConfig {
    private String roomName;
    private String decorations;
    private int thicknessWall;
    private int thicknessFloor;
    private boolean hideWalls;

    public RoomBundleConfig(String roomName, String decorations, int thicknessWall, int thicknessFloor, boolean hideWalls) {
        this.roomName = roomName;
        this.decorations = decorations;
        this.thicknessWall = thicknessWall;
        this.thicknessFloor = thicknessFloor;
        this.hideWalls = hideWalls;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDecorations() {
        return decorations;
    }

    public void setDecorations(String decorations) {
        this.decorations = decorations;
    }

    public int getThicknessWall() {
        return thicknessWall;
    }

    public void setThicknessWall(int thicknessWall) {
        this.thicknessWall = thicknessWall;
    }

    public int getThicknessFloor() {
        return thicknessFloor;
    }

    public void setThicknessFloor(int thicknessFloor) {
        this.thicknessFloor = thicknessFloor;
    }

    public boolean isHideWalls() {
        return hideWalls;
    }

    public void setHideWalls(boolean hideWalls) {
        this.hideWalls = hideWalls;
    }
}
