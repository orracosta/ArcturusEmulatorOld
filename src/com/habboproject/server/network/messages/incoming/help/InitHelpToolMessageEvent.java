package com.habboproject.server.network.messages.incoming.help;

import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.help.CallForHelpPendingCallsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class InitHelpToolMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.send(new CallForHelpPendingCallsMessageComposer(ModerationManager.getInstance().getActiveTicketByPlayerId(client.getPlayer().getId())));
    }
}
