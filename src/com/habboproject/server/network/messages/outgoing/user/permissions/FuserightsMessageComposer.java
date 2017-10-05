package com.habboproject.server.network.messages.outgoing.user.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class FuserightsMessageComposer extends MessageComposer {
    private final boolean hasClub;
    private final int rank;

    public FuserightsMessageComposer(final boolean hasClub, final int rank) {
        this.hasClub = hasClub;
        this.rank = rank;
    }

    @Override
    public short getId() {
        return Composers.UserRightsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(2);
        msg.writeInt(this.rank);
        msg.writeBoolean(false);// Is ambassador!
    }
}
