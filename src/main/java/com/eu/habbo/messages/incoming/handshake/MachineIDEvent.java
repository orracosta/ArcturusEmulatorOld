package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.MachineIDComposer;

/**
 * Created on 24-8-2014 17:20.
 */
public class MachineIDEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.packet.readString();
        this.client.sendResponse(new MachineIDComposer(this.packet.readString()));
    }
}
