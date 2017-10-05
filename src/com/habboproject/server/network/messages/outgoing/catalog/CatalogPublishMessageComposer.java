package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class CatalogPublishMessageComposer extends MessageComposer {

    private final boolean showNotification;

    public CatalogPublishMessageComposer(final boolean showNotification) {
        this.showNotification = showNotification;
    }

    @Override
    public short getId() {
        return Composers.CatalogUpdatedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.showNotification);
    }
}
