package com.habboproject.server.network.messages.outgoing.marketplace;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 02/02/2017.
 */
public class SettingsMessageComposer extends MessageComposer {
    private final int minPrice;
    private final int maxPrice;

    public SettingsMessageComposer(int minPrice, int maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public short getId() {
        return 866;
    }

    public void compose(IComposer msg) {
        msg.writeBoolean(true);
        msg.writeInt(this.minPrice);
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(this.maxPrice);
        msg.writeInt(48);
        msg.writeInt(7);
    }
}