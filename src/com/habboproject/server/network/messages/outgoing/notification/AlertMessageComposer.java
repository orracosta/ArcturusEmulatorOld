package com.habboproject.server.network.messages.outgoing.notification;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class AlertMessageComposer extends MessageComposer {
    private final String message;
    private final String link;

    public AlertMessageComposer(final String message, final String link) {
        this.message = message;
        this.link = link;
    }

    public AlertMessageComposer(final String message) {
        this(message, "");
    }

    @Override
    public short getId() {
        return Composers.BroadcastMessageAlertMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.message);
        msg.writeString(this.link);
    }
}
