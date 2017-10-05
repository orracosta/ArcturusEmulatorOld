package com.habboproject.server.network.messages.outgoing.notification;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class MotdNotificationMessageComposer extends MessageComposer {
    private final String message;

    public MotdNotificationMessageComposer(final String message) {
        this.message = message;
    }

    public MotdNotificationMessageComposer() {
        this(CometSettings.motdMessage);
    }

    @Override
    public short getId() {
        return Composers.MOTDNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeString(message);
    }
}
