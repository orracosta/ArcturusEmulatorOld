package com.habboproject.server.network.messages.outgoing.handshake;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class SecretKeyMessageComposer extends MessageComposer {
    private final String publicKey;

    public SecretKeyMessageComposer(final String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public short getId() {
        return Composers.SecretKeyMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.publicKey);
    }
}
