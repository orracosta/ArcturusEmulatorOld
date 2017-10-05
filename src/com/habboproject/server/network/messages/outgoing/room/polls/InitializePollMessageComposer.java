package com.habboproject.server.network.messages.outgoing.room.polls;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class InitializePollMessageComposer extends MessageComposer {

    private final int pollId;
    private final String headline;
    private final String thanksMessage;

    public InitializePollMessageComposer(int pollId, String headline, String thanksMessage) {
        this.pollId = pollId;
        this.headline = headline;
        this.thanksMessage = thanksMessage;
    }

    @Override
    public short getId() {
        return Composers.InitializePollMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.pollId);

        msg.writeString(this.headline);
        msg.writeString(this.headline);
        msg.writeString(this.headline);
        msg.writeString(this.thanksMessage);
    }
}
