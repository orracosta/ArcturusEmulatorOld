package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-8-2014 15:53.
 */
public class CatalogModeComposer extends MessageComposer {

    private int mode = 0;

    public CatalogModeComposer(int mode)
    {
        this.mode = mode;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.CatalogModeComposer);
        this.response.appendInt32(this.mode);
        return this.response;
    }
}
