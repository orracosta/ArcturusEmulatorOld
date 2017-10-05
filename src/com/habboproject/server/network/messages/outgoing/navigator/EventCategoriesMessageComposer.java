package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class EventCategoriesMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.NavigatorFlatCatsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);

        msg.writeInt(1);
        msg.writeString("Promoted Rooms");
        msg.writeBoolean(true);
    }
}
