package com.habboproject.server.network.messages.outgoing.handshake;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class AuthenticationOKMessageComposer extends MessageComposer {
    public AuthenticationOKMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.AuthenticationOKMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
