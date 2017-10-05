package com.habboproject.server.network.messages.incoming.handshake;

import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.handshake.InitCryptoMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class InitCryptoMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        final String p = client.getDiffieHellman().getPrime().toString();
        final String g = client.getDiffieHellman().getGenerator().toString();

        String encP = NetworkManager.getInstance().getRSA().sign(p);
        String encG = NetworkManager.getInstance().getRSA().sign(g);

        client.send(new InitCryptoMessageComposer(encP, encG));
    }
}
