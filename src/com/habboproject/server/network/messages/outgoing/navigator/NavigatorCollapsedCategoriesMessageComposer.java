package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class NavigatorCollapsedCategoriesMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.NavigatorCollapsedCategoriesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }
}
