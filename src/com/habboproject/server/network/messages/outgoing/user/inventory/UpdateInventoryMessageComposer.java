package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class UpdateInventoryMessageComposer extends MessageComposer {

    @Override
    public short getId() {
        return Composers.FurniListUpdateMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
