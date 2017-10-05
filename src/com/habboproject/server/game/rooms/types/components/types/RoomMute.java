package com.habboproject.server.game.rooms.types.components.types;

public class RoomMute {
    private int playerId;
    private int ticksLeft;

    public RoomMute(int playerId, int ticksLeft) {
        this.playerId = playerId;
        this.ticksLeft = ticksLeft;
    }

    public void decreaseTicks() {
        this.ticksLeft -= 1;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }
}
