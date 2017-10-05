package com.habboproject.server.network.messages.outgoing.group;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class UpdateFavouriteGroupMessageComposer extends MessageComposer {
    private final int playerId;

    public UpdateFavouriteGroupMessageComposer(final int playerId) {
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.RefreshFavouriteGroupMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerId);
    }
}
