package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class NavigatorMetaDataMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.NavigatorMetaDataParserMessageComposer;
    }

    public void compose(IComposer msg) {
        msg.writeInt(4);

        msg.writeString("official_view");
        msg.writeInt(0);

        msg.writeString("hotel_view");
        msg.writeInt(0);

        msg.writeString("roomads_view");
        msg.writeInt(0);

        msg.writeString("myworld_view");
        msg.writeInt(0);
    }
}
