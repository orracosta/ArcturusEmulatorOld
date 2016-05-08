package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 24-8-2014 17:42.
 */
public class DebugConsoleComposer extends MessageComposer {

    private static final boolean debugging = true;

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.DebugConsoleComposer);

        this.response.appendBoolean(true);

        return this.response;
    }
}
