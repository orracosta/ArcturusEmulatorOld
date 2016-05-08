package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 29-8-2014 15:16.
 */
public class AlertLimitedSoldOutComposer extends MessageComposer{

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.AlertLimitedSoldOutComposer);

        return this.response;
    }
}
