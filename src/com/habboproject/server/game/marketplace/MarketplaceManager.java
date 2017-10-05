package com.habboproject.server.game.marketplace;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferData;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.storage.queries.marketplace.MarketplaceDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by brend on 01/02/2017.
 */
public class MarketplaceManager implements Initializable {
    private static MarketplaceManager instance;

    private final Logger log = Logger.getLogger(MarketplaceManager.class.getName());
    private final Map<Integer, MarketplaceOfferData> offers = Maps.newHashMap();
    private final Map<Integer, Integer> purchasedOffers = Maps.newHashMap();

    public static MarketplaceManager getInstance() {
        if (instance == null) {
            instance = new MarketplaceManager();
        }
        return instance;
    }

    public void initialize() {
        List<MarketplaceOfferItem> offers = Lists.newArrayList();

        try {
            MarketplaceDao.loadOffers(offers);
        }
        catch (Exception e) {
            this.log.error("Error while loading marketplace offers", e);
        }

        offers.forEach(this::offerItem);
    }

    public void tick() {
        this.purchasedOffers.clear();
    }

    public void offerItem(MarketplaceOfferItem offerItem) {
        if (offerItem == null || offerItem.getDefinition() == null) {
            return;
        }

        if (!this.offers.containsKey(offerItem.getDefinition().getSpriteId())) {
            this.offers.put(offerItem.getDefinition().getSpriteId(), new MarketplaceOfferData(offerItem));
            return;
        }

        this.offers.get(offerItem.getDefinition().getSpriteId()).addOffer(offerItem);
    }

    public long getOffersSize(int spriteId) {
        return this.offers.get(spriteId).getItemsCount();
    }

    public void addPurchasedOffer(int spriteId) {
        if (this.purchasedOffers.containsKey(spriteId)) {
            this.purchasedOffers.replace(spriteId, this.purchasedOffers.get(spriteId) + 1);
            return;
        }
        this.purchasedOffers.put(spriteId, 1);
    }

    private List<MarketplaceOfferItem> getOffers(int minPrice, int maxPrice, String query) {
        ArrayList offers = Lists.newArrayList();

        this.offers.values().forEach(offerData -> {
                    MarketplaceOfferItem offerItem = offerData.getItemOffer(minPrice, maxPrice);
                    if (offerItem != null && offerItem.getTime() >= (int)(Comet.getTime() - 172800)) {
                        if (query.length() > 0) {
                            if (StringUtils.containsIgnoreCase(offerItem.getDefinition().getItemName(), query) ||
                                    StringUtils.containsIgnoreCase(offerItem.getDefinition().getPublicName(), query)) {
                                offers.add(offerItem);
                            }
                        } else {
                            offers.add(offerItem);
                        }
                    }
                }
        );

        return offers;
    }

    public List<MarketplaceOfferItem> getOffers(int minPrice, int maxPrice, String query, int sort) {
        List<MarketplaceOfferItem> offers;
        offers = this.getOffers(minPrice, maxPrice, query);

        List<MarketplaceOfferItem> offerItems = Lists.newArrayList();
        offerItems.addAll(offers);

        List<Integer> offersData = Lists.newArrayList();
        offersData.addAll(this.purchasedOffers.keySet());

        Map<Integer, Integer> itemTrades = Maps.newHashMap();
        itemTrades.putAll(this.purchasedOffers);

        try {
            switch (sort) {
                case 1: {
                    Collections.sort(offers, Collections.reverseOrder((o1, o2) -> Integer.valueOf(o1.getFinalPrice()).compareTo(o2.getFinalPrice())));
                    break;
                }

                case 2: {
                    Collections.sort(offers, (o1, o2) -> Integer.valueOf(o1.getFinalPrice()).compareTo(o2.getFinalPrice()));
                    break;
                }

                case 3: {
                    Collections.sort(offersData, Collections.reverseOrder((o1, o2) -> {
                                Integer i1 = (Integer)itemTrades.get(o1);
                                Integer i2 = (Integer)itemTrades.get(o2);
                                return i1.compareTo(i2);
                            }
                    ));
                    offers.clear();
                    for (Integer spriteId : offersData) {
                        offers.addAll(offerItems.stream().filter(x -> x != null && x.getDefinition().getSpriteId() == spriteId.intValue()).collect(Collectors.toList()));
                    }
                    break;
                }

                case 4: {
                    Collections.sort(offersData, (o1, o2) -> {
                                Integer i1 = (Integer)itemTrades.get(o1);
                                Integer i2 = (Integer)itemTrades.get(o2);
                                return i1.compareTo(i2);
                            }
                    );

                    offers.clear();
                    for (Integer spriteId : offersData) {
                        offers.addAll(offerItems.stream().filter(x -> x != null && x.getDefinition().getSpriteId() == spriteId.intValue()).collect(Collectors.toList()));
                    }
                    break;
                }

                case 5: {
                    Collections.sort(offers, Collections.reverseOrder((o1, o2) -> {
                                Integer i1 = (int)this.offers.get(o1.getDefinition().getSpriteId()).getItemsCount();
                                Integer i2 = (int)this.offers.get(o2.getDefinition().getSpriteId()).getItemsCount();
                                return i1.compareTo(i2);
                            }
                    ));
                    break;
                }

                case 6: {
                    Collections.sort(offers, (o1, o2) -> {
                                Integer i1 = (int)this.offers.get(o1.getDefinition().getSpriteId()).getItemsCount();
                                Integer i2 = (int)this.offers.get(o2.getDefinition().getSpriteId()).getItemsCount();
                                return i1.compareTo(i2);
                            }
                    );
                }

                default: {
                    break;
                }
            }
        }
        catch (Exception e) {

        }

        return offers;
    }

    public List<MarketplaceOfferItem> getOwnOffers(int playerId) {
        ArrayList offers = Lists.newArrayList();
        this.offers.values().stream().filter(x -> x != null && x.hasOfferOfPlayer(playerId)).forEach(x -> {
                    offers.addAll(x.getOffersOfPlayer(playerId));
                }
        );
        return offers;
    }

    public List<MarketplaceOfferItem> getOwnSoldOffers(int playerId) {
        ArrayList offers = Lists.newArrayList();
        this.getOwnOffers(playerId).stream().filter(x -> x != null && x.getState() == 2).forEach(offer -> {
                    boolean bl = offers.add(offer);
                }
        );
        return offers;
    }

    public int getSoldPriceForPlayer(List<MarketplaceOfferItem> offers) {
        int total = 0;
        for (MarketplaceOfferItem offer : offers) {
            if (offer.getState() != 2) continue;
            total += offer.getPrice();
        }
        return total;
    }

    public int getAvaragePrice(int spriteId) {
        return this.offers.get(spriteId).getAvaragePrice();
    }

    public int getOffersByItem(int spriteId) {
        return (int)this.offers.get(spriteId).getItemsCount();
    }

    public ItemDefinition getDefinitionById(int offerId) {
        int itemId = MarketplaceDao.getItemIdByOffer((int)offerId);
        return ItemManager.getInstance().getDefinition(itemId);
    }

    public MarketplaceOfferItem getOfferById(int offerId) {
        ItemDefinition definition = this.getDefinitionById(offerId);
        if (definition != null) {
            return this.offers.get(definition.getSpriteId()).getItemOffer(offerId);
        }
        return null;
    }

    public void endOffer(int offerId) {
        ItemDefinition definition = this.getDefinitionById(offerId);
        if (definition != null) {
            this.offers.get(definition.getSpriteId()).removeOffer(offerId);
        }
    }
}
