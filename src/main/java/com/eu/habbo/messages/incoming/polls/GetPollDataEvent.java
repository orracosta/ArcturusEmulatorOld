package com.eu.habbo.messages.incoming.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.polls.PollQuestionsComposer;

/**
 * Created on 3-11-2014 19:16.
 */
public class GetPollDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int pollId = this.packet.readInt();

        Poll poll = PollManager.getPoll(pollId);

        if(poll != null)
        {
            this.client.sendResponse(new PollQuestionsComposer(poll));
        }
    }
}
