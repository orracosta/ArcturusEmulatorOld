package com.habboproject.server.game.marketplace.types;

import com.habboproject.server.storage.queries.marketplace.MarketplaceDao;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by brend on 01/02/2017.
 */
public class MarketplaceOfferData {
    private final int spriteId;
    private final List<MarketplaceOfferItem> items = Lists.newArrayList();

    public MarketplaceOfferData(MarketplaceOfferItem offerItem) {
        this.spriteId = offerItem.getDefinition().getSpriteId();
        this.addOffer(offerItem);
    }

    public void addOffer(MarketplaceOfferItem offerItem) {
        if (offerItem == null) {
            return;
        }
        this.items.add(offerItem);
    }

    public void removeOffer(int offerId) {
        this.removeOffer(this.getItemOffer(offerId));
    }

    public void removeOffer(MarketplaceOfferItem offerItem) {
        if (offerItem == null) {
            return;
        }
        this.items.remove(offerItem);
        MarketplaceDao.deleteOffer((MarketplaceOfferItem)offerItem);
    }

    public MarketplaceOfferItem getItemOffer(int offerId) {
        for (MarketplaceOfferItem offer : this.items) {
            if (offer.getOfferId() != offerId) continue;
            return offer;
        }
        return null;
    }

    public MarketplaceOfferItem getItemOffer(int minPrice, int maxPrice) {
        Collections.sort(this.items, Collections.reverseOrder((o1, o2) -> Integer.valueOf(o1.getTime()).compareTo(o2.getTime())));
        for (MarketplaceOfferItem offerItem : this.items.stream().filter(x -> x.getState() == 1).collect(Collectors.toList())) {
            if (minPrice > 0 && offerItem.getFinalPrice() < minPrice || maxPrice > 0 && offerItem.getFinalPrice() > maxPrice)
                continue;

            return offerItem;
        }
        return null;
    }

    public boolean hasOfferOfPlayer(int playerId) {
        if (this.items.stream().filter(x -> x != null && x.getPlayerId() == playerId).count() > 0) {
            return true;
        }
        return false;
    }

    public List<MarketplaceOfferItem> getOffersOfPlayer(int playerId) {
        return this.items.stream().filter(x -> x != null && x.getPlayerId() == playerId).collect(Collectors.toList());
    }

    public int getAvaragePrice() {
        int avgPrice = 0;
        for (MarketplaceOfferItem offerItem : this.items) {
            avgPrice += offerItem.getFinalPrice();
        }
        return avgPrice / this.items.size();
    }

    public long getItemsCount() {
        return this.items.stream().filter(x -> x != null && x.getState() == 1).count();
    }

    public int getSpriteId() {
        return this.spriteId;
    }
}
