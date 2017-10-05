package com.habboproject.server.game.players.components.types.messenger;

public enum RelationshipLevel {
    BOBBA(3),
    SMILE(2),
    HEART(1);

    private int levelId;

    RelationshipLevel(int id) {
        this.levelId = id;
    }

    public int getLevelId() {
        return levelId;
    }
}
