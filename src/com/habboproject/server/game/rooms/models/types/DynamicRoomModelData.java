package com.habboproject.server.game.rooms.models.types;

public class DynamicRoomModelData {
    private String name;
    private String heightmap;
    private int doorX;
    private int doorY;
    private int doorZ;
    private int doorRotation;
    private int wallHeight;

    public DynamicRoomModelData(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) {
        this.name = name;
        this.heightmap = heightmap;
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorRotation = doorRotation;
        this.wallHeight = wallHeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeightmap() {
        return heightmap;
    }

    public void setHeightmap(String heightmap) {
        this.heightmap = heightmap;
    }

    public int getDoorX() {
        return doorX;
    }

    public void setDoorX(int doorX) {
        this.doorX = doorX;
    }

    public int getDoorY() {
        return doorY;
    }

    public void setDoorY(int doorY) {
        this.doorY = doorY;
    }

    public int getDoorZ() {
        return doorZ;
    }

    public void setDoorZ(int doorZ) {
        this.doorZ = doorZ;
    }

    public int getDoorRotation() {
        return doorRotation;
    }

    public void setDoorRotation(int doorRotation) {
        this.doorRotation = doorRotation;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    public void setWallHeight(int wallHeight) {
        this.wallHeight = wallHeight;
    }
}
