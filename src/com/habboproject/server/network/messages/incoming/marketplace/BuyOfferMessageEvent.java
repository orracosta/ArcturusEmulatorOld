package com.habboproject.server.network.messages.incoming.marketplace;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogPublishMessageComposer;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.marketplace.BuyOfferMessageComposer;
import com.habboproject.server.network.messages.outgoing.marketplace.CancelOfferMessageComposer;
import com.habboproject.server.network.messages.outgoing.marketplace.OffersMessageComposer;
import com.habboproject.server.network.messages.outgoing.marketplace.OwnOffersMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.items.ItemDao;
import com.habboproject.server.storage.queries.items.LimitedEditionDao;

/**
 * Created by brend on 31/01/2017.
 */
public class BuyOfferMessageEvent implements Event {
    @Override
    public void handle(final Session client, final MessageEvent msg) throws Exception {
        int offerId = msg.readInt();

        MarketplaceOfferItem offer = MarketplaceManager.getInstance().getOfferById(offerId);

        if (offer == null) {
            client.send(new OffersMessageComposer(MarketplaceManager.getInstance().getOffers(0, 0, "", 1)));
            return;
        }

        if (offer.getState() == 2) {
            client.send(new OffersMessageComposer(MarketplaceManager.getInstance().getOffers(0, 0, "", 1)));
            return;
        }

        if (this.getCorrectTime(offer.getTime()) <= 0) {
            offer.stateUpdate(3);
            client.send(new OffersMessageComposer(MarketplaceManager.getInstance().getOffers(0, 0, "", 1)));
            return;
        }

        if (offer.getPlayerId() == client.getPlayer().getId()) {
            long itemId = ItemDao.createItem(client.getPlayer().getId(), offer.getDefinition().getId(), "");

            PlayerItem playerItem = client.getPlayer().getInventory().add(itemId, offer.getDefinition().getId(), 0, "",
                    null, (offer.getLimitedNumber() > 0 ?
                            new LimitedEditionItemData(itemId, offer.getLimitedStack(), offer.getLimitedNumber()) : null));

            if (offer.getLimitedStack() > 0) {
                LimitedEditionDao.save(new LimitedEditionItemData(itemId, offer.getLimitedStack(), offer.getLimitedNumber()));
            }

            client.send(new UnseenItemsMessageComposer(playerItem));
            client.send(new UpdateInventoryMessageComposer());

            MarketplaceManager.getInstance().endOffer(offerId);

            client.send(new CancelOfferMessageComposer(offerId, true));
            client.send(new OffersMessageComposer(MarketplaceManager.getInstance().getOffers(0, 0, "", 1)));
            return;
        }

        int balance = 0;
        String defaultMarketplaceCoin;
        switch (defaultMarketplaceCoin = CometSettings.defaultMarketplaceCoin) {
            case "diamonds": {
                balance = client.getPlayer().getData().getVipPoints();
                break;
            }
            case "credits": {
                balance = client.getPlayer().getData().getCredits();
                break;
            }
            case "duckets": {
                balance = client.getPlayer().getData().getActivityPoints();
                break;
            }
            default:
                break;
        }

        if (balance < offer.getFinalPrice()) {
            client.send(new BuyOfferMessageComposer(3, offerId, offer.getFinalPrice()));
            return;
        }

        long itemId2 = ItemDao.createItem(client.getPlayer().getId(), offer.getDefinition().getId(), "");

        offer.stateUpdate(2);

        PlayerItem playerItem2 = client.getPlayer().getInventory().add(itemId2, offer.getDefinition().getId(), 0, "", null, ((offer.getLimitedNumber() > 0) ? new LimitedEditionItemData(itemId2, offer.getLimitedStack(), offer.getLimitedNumber()) : null));

        if (offer.getLimitedStack() > 0) {
            LimitedEditionDao.save(new LimitedEditionItemData(itemId2, offer.getLimitedStack(), offer.getLimitedNumber()));
        }

        String defaultMarketplaceCoin2;
        switch (defaultMarketplaceCoin2 = CometSettings.defaultMarketplaceCoin) {
            case "diamonds": {
                client.getPlayer().getData().decreasePoints(offer.getFinalPrice());
                client.getPlayer().sendBalance();
                break;
            }
            case "credits": {
                client.getPlayer().getData().decreaseCredits(offer.getFinalPrice());
                client.getPlayer().sendBalance();
                break;
            }
            case "duckets": {
                client.getPlayer().getData().decreaseActivityPoints(offer.getFinalPrice());
                client.send(new UpdateActivityPointsMessageComposer(client.getPlayer().getData().getActivityPoints(), offer.getFinalPrice()));
                break;
            }
            default:
                break;
        }

        client.send(new UnseenItemsMessageComposer(playerItem2));
        client.send(new UpdateInventoryMessageComposer());

        if (MarketplaceManager.getInstance().getOffersSize(offer.getDefinition().getSpriteId()) > 1L) {
            MarketplaceManager.getInstance().addPurchasedOffer(offer.getDefinition().getSpriteId());
        }

        client.send(new BuyOfferMessageComposer(1, offerId, offer.getFinalPrice()));
        Session user = NetworkManager.getInstance().getSessions().getByPlayerId(offer.getPlayerId());
        if (user != null) {
            user.send(new CatalogPublishMessageComposer(false));
            user.send(new NotificationMessageComposer("furni_placement_error", Locale.get("marketplace.sell.message")));
        }
    }

    private int getCorrectTime(final int time) {
        return time + 172800 - (int)Comet.getTime();
    }
}
