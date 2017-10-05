package com.habboproject.server.network.messages.outgoing.marketplace;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.network.messages.composers.MessageComposer;

import java.util.List;

/**
 * Created by brend on 02/02/2017.
 */
public class OwnOffersMessageComposer extends MessageComposer {
    private final List<MarketplaceOfferItem> offers;
    private final int soldPrice;

    public OwnOffersMessageComposer(List<MarketplaceOfferItem> offers) {
        this.offers = offers;
        this.soldPrice = MarketplaceManager.getInstance().getSoldPriceForPlayer(offers);
    }

    public short getId() {
        return 2743;
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.soldPrice);
        msg.writeInt(this.offers.size());
        this.offers.forEach(offer -> {
                    int correctTime = this.getCorrectTime(offer.getTime());
                    if (offer.getState() == 1 && correctTime <= 0) {
                        offer.stateUpdate(3);
                    }
                    msg.writeInt(offer.getOfferId());
                    msg.writeInt(offer.getState());
                    msg.writeInt(offer.getType());
                    msg.writeInt(offer.getDefinition().getSpriteId());
                    switch (offer.getType()) {
                        case 1:
                        case 2: {
                            msg.writeInt(0);
                            msg.writeString("");
                            break;
                        }
                        case 3: {
                            msg.writeInt(offer.getLimitedStack());
                            msg.writeInt(offer.getLimitedNumber());
                        }
                    }
                    msg.writeInt(offer.getPrice());
                    msg.writeInt(offer.getState() != 1 ? 0 : correctTime / 60);
                    msg.writeInt(0);
                }
        );
    }

    private int getCorrectTime(int time) {
        return time + 172800 - (int) Comet.getTime();
    }
}