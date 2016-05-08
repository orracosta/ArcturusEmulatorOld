package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 24-8-2014 17:39.
 */
public class SessionRightsComposer extends MessageComposer {

    private static final boolean unknownBooleanOne = true; //true
    private static final boolean unknownBooleanTwo = false;

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.SessionRightsComposer);

        this.response.appendBoolean(unknownBooleanOne);
        this.response.appendBoolean(unknownBooleanTwo);

        return this.response;
    }
}
