package com.habboproject.server.network.messages.outgoing.messenger;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class MessengerConfigMessageComposer extends MessageComposer {

    @Override
    public short getId() {
        return Composers.MessengerInitMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(CometSettings.messengerMaxFriends);
        msg.writeInt(300);
        msg.writeInt(800);
        msg.writeInt(0);
    }
}
