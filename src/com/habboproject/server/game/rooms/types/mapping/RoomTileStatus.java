package com.habboproject.server.game.rooms.types.mapping;

public class RoomTileStatus {
    private RoomTileStatusType statusType;
    private int effectId;

    private int positionX;
    private int positionY;
    private int rotation;

    private double interactionHeight;

    public RoomTileStatus(RoomTileStatusType type, int effectId, int positionX, int positionY, int rotation, double interactionHeight) {
        this.statusType = type;
        this.effectId = effectId;

        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;

        this.interactionHeight = interactionHeight;
    }

    public double getInteractionHeight() {
        return this.interactionHeight;
    }
}
