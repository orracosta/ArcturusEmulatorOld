package com.habboproject.server.game.rooms.models;

public class CustomModel {
    private final int doorX;
    private final int doorY;
    private final int doorZ;
    private final int doorRotation;
    private final int wallHeight;
    private final String modelData;

    public CustomModel(int doorX, int doorY, int doorZ, int doorRotation, String modelData, int wallHeight) {
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorRotation = doorRotation;
        this.modelData = modelData;
        this.wallHeight = wallHeight;
    }

    public int getDoorX() {
        return this.doorX;
    }

    public int getDoorY() {
        return this.doorY;
    }

    public int getDoorZ() {
        return this.doorZ;
    }

    public int getDoorRotation() {
        return this.doorRotation;
    }

    public String getModelData() {
        return this.modelData;
    }

    public int getWallHeight() {
        return this.wallHeight;
    }
}
