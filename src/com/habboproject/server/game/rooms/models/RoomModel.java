package com.habboproject.server.game.rooms.models;

import com.habboproject.server.api.networking.messages.IMessageComposer;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.game.utilities.ModelUtils;
import com.habboproject.server.network.messages.outgoing.room.engine.RelativeHeightmapMessageComposer;
import org.apache.log4j.Logger;


public abstract class RoomModel {
    private String name;
    private String map = "";

    private int doorX;
    private int doorY;
    private int doorZ;
    private int doorRotation;

    private int mapSizeX;
    private int mapSizeY;

    private double[][] squareHeight;
    private RoomTileState[][] squareState;

    private final IMessageComposer floorMapMessageComposer;

    private int wallHeight;

    public RoomModel(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRotation, int wallHeight) throws InvalidModelException {
        this.name = name;
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorRotation = doorRotation;
        this.wallHeight = wallHeight;

        String[] mapData = heightmap.split(String.valueOf((char) 13));

        if (mapData.length == 0) throw new InvalidModelException();

        this.mapSizeX = mapData[0].length();
        this.mapSizeY = mapData.length;
        this.squareHeight = new double[mapSizeX][mapSizeY];
        this.squareState = new RoomTileState[mapSizeX][mapSizeY];

        int maxTileHeight = 0;

        try {
            for (int y = 0; y < mapSizeY; y++) {
                char[] line = mapData[y].replace("\r", "").replace("\n", "").toCharArray();

                int x = 0;
                for (char tile : line) {
                    if (x >= mapSizeX) {
                        throw new InvalidModelException();
                    }

                    String tileVal = String.valueOf(tile);

                    if (tileVal.equals("x")) {
                        squareState[x][y] = (x == doorX && y == doorY) ? RoomTileState.VALID : RoomTileState.INVALID;
                    } else {
                        squareState[x][y] = RoomTileState.VALID;
                        squareHeight[x][y] = (double) ModelUtils.getHeight(tile);

                        if (squareHeight[x][y] > maxTileHeight) {
                            maxTileHeight = (int) Math.ceil(squareHeight[x][y]);
                        }
                    }

                    x++;
                }
            }

            for (String mapLine : heightmap.split("\r\n")) {
                if (mapLine.isEmpty()) {
                    continue;
                }
                map += mapLine + (char) 13;
            }
        } catch (Exception e) {
            if (e instanceof InvalidModelException) {
                throw e;
            }

            Logger.getLogger(RoomModel.class.getName()).error("Failed to parse heightmap for model: " + this.name, e);
        }

        if (maxTileHeight >= 29) {
            this.wallHeight = 15;
        }

        this.floorMapMessageComposer = new RelativeHeightmapMessageComposer(this);
    }

    public String getId() {
        return this.name;
    }

    public String getMap() {
        return this.map;
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

    public void setDoorZ(int doorZ) {
        this.doorZ = doorZ;
    }

    public int getDoorRotation() {
        return this.doorRotation;
    }

    public int getSizeX() {
        return this.mapSizeX;
    }

    public int getSizeY() {
        return this.mapSizeY;
    }

    public RoomTileState[][] getSquareState() {
        return this.squareState;
    }

    public double[][] getSquareHeight() {
        return this.squareHeight;
    }

    public IMessageComposer getRelativeHeightmapMessage() {
        return this.floorMapMessageComposer;
    }

    public int getWallHeight() {
        return wallHeight;
    }

    public class InvalidModelException extends Exception {

    }
}
