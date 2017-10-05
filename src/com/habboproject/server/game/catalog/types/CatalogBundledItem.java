package com.habboproject.server.game.catalog.types;

public class CatalogBundledItem {

    private final int itemId;
    private final int amount;
    private final String presetData;

    public CatalogBundledItem(String presetData, int amount, int itemId) {
        this.presetData = presetData;
        this.amount = amount;
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public String getPresetData() {
        return presetData;
    }

}