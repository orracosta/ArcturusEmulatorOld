package com.habboproject.server.network.messages.outgoing.room.alerts;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class DoorbellNoAnswerComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.FlatAccessDeniedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString("");
    }
}
