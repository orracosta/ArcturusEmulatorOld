package com.habboproject.server.network.messages.incoming.marketplace;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.List;

/**
 * Created by brend on 31/01/2017.
 */
public class RedeemCoinsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        List<MarketplaceOfferItem> myOffers = MarketplaceManager.getInstance().getOwnSoldOffers(client.getPlayer().getId());
        int owedCoins = this.getOwedCoins(myOffers);

        if (owedCoins >= 1) {
            final String defaultMarketplaceCoin;
            switch (defaultMarketplaceCoin = CometSettings.defaultMarketplaceCoin) {
                case "diamonds": {
                    client.getPlayer().getData().increasePoints(owedCoins);
                    client.getPlayer().sendBalance();
                    break;
                }

                case "credits": {
                    client.getPlayer().getData().increaseCredits(owedCoins);
                    client.getPlayer().sendBalance();
                    break;
                }

                case "duckets": {
                    client.getPlayer().getData().increaseActivityPoints(owedCoins);
                    client.send(new UpdateActivityPointsMessageComposer(client.getPlayer().getData().getActivityPoints(), owedCoins));
                    break;
                }

                default:
                    break;
            }
        }

        myOffers.forEach(offer -> MarketplaceManager.getInstance().endOffer(offer.getOfferId()));
    }

    private int getOwedCoins(List<MarketplaceOfferItem> offers) {
        return MarketplaceManager.getInstance().getSoldPriceForPlayer(offers);
    }
}
