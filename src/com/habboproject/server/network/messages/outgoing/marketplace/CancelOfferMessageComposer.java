package com.habboproject.server.network.messages.outgoing.marketplace;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 02/02/2017.
 */
public class CancelOfferMessageComposer extends MessageComposer {
    private final int offerId;
    private final boolean success;

    public CancelOfferMessageComposer(int offerId, boolean success) {
        this.offerId = offerId;
        this.success = success;
    }

    public short getId() {
        return 1425;
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.offerId);
        msg.writeBoolean(Boolean.valueOf(this.success));
    }
}