package com.habboproject.server.game.catalog.types;

public class CatalogOffer {
    private int offerId;
    private int catalogPageId;
    private int catalogItemId;

    public CatalogOffer(int offerId, int catalogPageId, int catalogItemId) {
        this.offerId = offerId;
        this.catalogPageId = catalogPageId;
        this.catalogItemId = catalogItemId;
    }

    public int getOfferId() {
        return offerId;
    }

    public int getCatalogPageId() {
        return catalogPageId;
    }

    public int getCatalogItemId() {
        return catalogItemId;
    }
}
