package com.habboproject.server.network.messages.outgoing.notification;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class AdvancedAlertMessageComposer extends MessageComposer {
    private final String title;
    private final String message;
    private final String linkName;
    private final String linkLocation;
    private final String illustration;

    public AdvancedAlertMessageComposer(final String title, final String message, final String linkName, final String linkLocation, final String illustration) {
        this.title = title;
        this.message = message;
        this.linkName = linkName;
        this.linkLocation = linkLocation;
        this.illustration = illustration;
    }

    public AdvancedAlertMessageComposer(final String header, final String message) {
        this(header, message, "", "", "");
    }

    public AdvancedAlertMessageComposer(final String header, final String message, final String image) {
        this(header, message, "", "", image);
    }

    public AdvancedAlertMessageComposer(String message) {
        this("Alert", message, "", "", "");
    }

    @Override
    public short getId() {
        return Composers.RoomNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(illustration);

        msg.writeInt(linkLocation.isEmpty() ? 2 : 4);
        msg.writeString("title");
        msg.writeString(title);
        msg.writeString("message");
        msg.writeString(message);

        if (!linkLocation.isEmpty()) {
            msg.writeString("linkUrl");
            msg.writeString(linkLocation);
            msg.writeString("linkTitle");
            msg.writeString(linkName);
        }
    }
}
