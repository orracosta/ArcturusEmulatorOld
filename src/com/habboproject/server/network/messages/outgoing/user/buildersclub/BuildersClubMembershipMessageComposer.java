package com.habboproject.server.network.messages.outgoing.user.buildersclub;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class BuildersClubMembershipMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.BuildersClubMembershipMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(999999999);
        msg.writeInt(100);
        msg.writeInt(2);
    }
}
