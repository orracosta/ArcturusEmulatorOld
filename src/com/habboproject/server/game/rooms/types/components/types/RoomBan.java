package com.habboproject.server.game.rooms.types.components.types;

public class RoomBan {
    private int playerId;
    private String playerName;
    private int ticksLeft;

    private boolean isPermanent;

    public RoomBan(int playerId, String playerName, int ticksLeft) {
        this.playerId = playerId;
        this.ticksLeft = ticksLeft;
        this.playerName = playerName;

        this.isPermanent = this.ticksLeft == -1;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void decreaseTicks() {
        this.ticksLeft--;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public String getPlayerName() {
        return playerName;
    }
}
