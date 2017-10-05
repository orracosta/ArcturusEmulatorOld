package com.habboproject.server.network.messages.outgoing.catalog.data;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class CatalogOfferConfigMessageComposer extends MessageComposer {

    public CatalogOfferConfigMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.CatalogItemDiscountMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(100);
        msg.writeInt(6);
        msg.writeInt(1);
        msg.writeInt(1);
        msg.writeInt(2);
        msg.writeInt(40);
        msg.writeInt(99);
    }
}
