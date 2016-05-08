package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 24-8-2014 16:50.
 */
public class GenerateSecretKeyEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.packet.readString();
    }
}
