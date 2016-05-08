package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 24-8-2014 17:16.
 */
public class SecureLoginOKComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.SecureLoginOKComposer);

        return this.response;
    }
}
