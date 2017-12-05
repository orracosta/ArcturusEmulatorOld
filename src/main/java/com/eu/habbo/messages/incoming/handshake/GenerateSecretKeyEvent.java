package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;

public class GenerateSecretKeyEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.packet.readString();
    }
}
