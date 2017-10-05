package com.habboproject.server.network.messages.incoming.help.tool;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class OpenGuideToolMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final boolean onDuty = msg.readBoolean();

        final boolean handleTourRequests = msg.readBoolean();
        final boolean handleHelpRequests = msg.readBoolean();
        final boolean handleBullyRequests = msg.readBoolean();

        if (onDuty) {
            // we're on duty!!
        }

        // 2200
        client.send(new MessageComposer() {
            @Override
            public short getId() {
                return 2200;
            }

            @Override
            public void compose(IComposer msg) {
                msg.writeBoolean(onDuty);
                msg.writeInt(onDuty ? 1 : 0);
                msg.writeInt(0);
                msg.writeInt(0);
            }
        });
    }
}
