package com.habboproject.server.network.messages.outgoing.marketplace;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 02/02/2017.
 */
public class CanMakeOfferMessageComposer extends MessageComposer {
    private final int code;

    public CanMakeOfferMessageComposer(int code) {
        this.code = code;
    }

    public short getId() {
        return 219;
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.code);
        msg.writeInt(0);
    }
}