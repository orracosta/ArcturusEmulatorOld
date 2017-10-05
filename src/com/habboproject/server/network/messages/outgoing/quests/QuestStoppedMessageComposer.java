package com.habboproject.server.network.messages.outgoing.quests;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class QuestStoppedMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.QuestAbortedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(false); //???
    }
}
