package com.habboproject.server.game.players.components.types.inventory;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItemSnapshot;

public class InventoryItemSnapshot implements PlayerItemSnapshot {
    private long id;
    private int baseItemId;
    private String extraData;

    public InventoryItemSnapshot(long id, int baseItemId, String extraData) {
        this.id = id;
        this.baseItemId = baseItemId;
        this.extraData = extraData;
    }

    public long getId() {
        return id;
    }

    public int getBaseItemId() {
        return baseItemId;
    }

    public String getExtraData() {
        return extraData;
    }
}
