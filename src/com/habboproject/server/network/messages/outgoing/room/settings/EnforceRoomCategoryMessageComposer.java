package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class EnforceRoomCategoryMessageComposer extends MessageComposer {

    private int defaultCategory = 16;

    public EnforceRoomCategoryMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.EnforceCategoryUpdateMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(defaultCategory);
    }
}
