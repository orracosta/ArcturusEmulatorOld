package com.habboproject.server.network.messages.outgoing.handshake;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class InitCryptoMessageComposer extends MessageComposer {
    private final String prime;
    private final String generator;

    public InitCryptoMessageComposer(final String prime, final String generator) {
        this.prime = prime;
        this.generator = generator;
    }

    @Override
    public short getId() {
        return Composers.InitCryptoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.prime);
        msg.writeString(this.generator);
    }
}
