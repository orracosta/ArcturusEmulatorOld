package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class GiftUserNotFoundMessageComposer extends MessageComposer {

    public GiftUserNotFoundMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.GiftWrappingErrorMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
