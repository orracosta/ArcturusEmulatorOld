package com.habboproject.server.game.players.components.types.inventory;

/**
 * Created by brend on 01/02/2017.
 */
public class InventoryEffect {
    private final int id;
    private final int effectId;
    private final double duration;
    private final boolean isTotem;
    private boolean activated;
    private double timestampActivated;
    private int quantity;

    public InventoryEffect(int id, int effectId, double duration, boolean isTotem, boolean activated, double timestampActivated, int quantity) {
        this.id = id;
        this.effectId = effectId;
        this.duration = duration;
        this.isTotem = isTotem;
        this.activated = activated;
        this.timestampActivated = timestampActivated;
        this.quantity = quantity;
    }

    public int getId() {
        return this.id;
    }

    public int getEffectId() {
        return this.effectId;
    }

    public double getDuration() {
        return this.duration;
    }

    public boolean isTotem() {
        return this.isTotem;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public double getTimestampActivated() {
        return this.timestampActivated;
    }

    public void setTimestampActivated(double timestampActivated) {
        this.timestampActivated = timestampActivated;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
