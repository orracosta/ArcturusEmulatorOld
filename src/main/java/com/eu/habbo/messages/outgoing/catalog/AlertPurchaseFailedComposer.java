package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 29-8-2014 15:51.
 */
public class AlertPurchaseFailedComposer extends MessageComposer {

    public static final int SERVER_ERROR = 0;
    public static final int ALREADY_HAVE_BADGE = 1;

    private final int error;

    public AlertPurchaseFailedComposer(int error)
    {
        this.error = error;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.AlertPurchaseFailedComposer);
        this.response.appendInt32(this.error);
        return this.response;
    }
}
