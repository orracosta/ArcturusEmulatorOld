package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class FavouriteRoomsMessageComposer extends MessageComposer {
    private static final int MAX_FAVOURITE_ROOMS = 30;

    @Override
    public short getId() {
        return Composers.FavouritesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(MAX_FAVOURITE_ROOMS);
        msg.writeInt(0);//size
    }
}
