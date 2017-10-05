package com.habboproject.server.network.messages.outgoing.messenger;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class InstantChatMessageComposer extends MessageComposer {
    private final String message;
    private final int fromId;

    public InstantChatMessageComposer(final String message, final int fromId) {
        this.message = message;
        this.fromId = fromId;
    }

    @Override
    public short getId() {
        return Composers.NewConsoleMessageMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(fromId);
        msg.writeString(message);
        msg.writeInt(0);
    }
}
