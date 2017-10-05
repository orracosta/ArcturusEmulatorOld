package com.habboproject.server.network.messages.incoming.polls;

import com.habboproject.server.game.polls.PollManager;
import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.polls.PollMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class GetPollMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int pollId = msg.readInt();

        Poll poll = PollManager.getInstance().getPollbyId(pollId);

        if(poll == null) {
            return;
        }

        client.send(new PollMessageComposer(poll));
    }
}