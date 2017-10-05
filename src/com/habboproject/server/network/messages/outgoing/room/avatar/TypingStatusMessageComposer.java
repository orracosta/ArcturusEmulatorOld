package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class TypingStatusMessageComposer extends MessageComposer {
    private final int playerId;
    private final int status;

    public TypingStatusMessageComposer(final int playerId, final int status) {
        this.playerId = playerId;
        this.status = status;
    }

    @Override
    public short getId() {
        return Composers.UserTypingMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeInt(status);
    }
}
