package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-8-2014 15:36.
 */
public class CatalogUpdatedComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.CatalogUpdatedComposer);
        this.response.appendBoolean(false);
        return this.response;
    }
}
