package com.habboproject.server.game.effects.types;

/**
 * Created by brend on 01/02/2017.
 */
public class EffectItem {
    private final int effectId;
    private final int minRank;
    private final boolean furniEffect;
    private final boolean buyableEffect;

    public EffectItem(int effectId, int minRank, boolean furniEffect, boolean buyableEffect) {
        this.effectId = effectId;
        this.minRank = minRank;
        this.furniEffect = furniEffect;
        this.buyableEffect = buyableEffect;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getMinRank() {
        return minRank;
    }

    public boolean isFurniEffect() {
        return furniEffect;
    }

    public boolean isBuyableEffect() {
        return buyableEffect;
    }
}
