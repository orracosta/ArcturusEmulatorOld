package com.habboproject.server.network.messages.outgoing.messenger;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class InviteFriendMessageComposer extends MessageComposer {
    private final String message;
    private final int fromId;

    public InviteFriendMessageComposer(final String message, final int fromId) {
        this.message = message;
        this.fromId = fromId;
    }

    @Override
    public short getId() {
        return Composers.RoomInviteMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(fromId);
        msg.writeString(message);
    }
}
