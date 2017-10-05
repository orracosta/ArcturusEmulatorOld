package com.habboproject.server.network.messages.outgoing.marketplace;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 02/02/2017.
 */
public class BuyOfferMessageComposer extends MessageComposer {
    private final int code;
    private final int offerId;
    private final int price;

    public BuyOfferMessageComposer(int code, int offerId, int price) {
        this.code = code;
        this.offerId = offerId;
        this.price = price;
    }

    public short getId() {
        return 1538;
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.code);
        msg.writeInt(0);
        msg.writeInt(this.offerId);
        msg.writeInt(this.price);
    }
}