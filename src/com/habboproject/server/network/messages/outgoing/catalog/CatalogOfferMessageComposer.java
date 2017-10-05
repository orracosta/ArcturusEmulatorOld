package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class CatalogOfferMessageComposer extends MessageComposer {
    private final CatalogItem catalogItem;

    public CatalogOfferMessageComposer(final CatalogItem catalogItem) {
        this.catalogItem = catalogItem;
    }

    @Override
    public short getId() {
        return Composers.CatalogOfferMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        this.catalogItem.compose(msg);
    }
}
