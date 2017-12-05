package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollQuestion;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PollQuestionsComposer extends MessageComposer
{
    private Poll poll;

    public PollQuestionsComposer(Poll poll)
    {
        this.poll = poll;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PollQuestionsComposer);

        this.response.appendInt(this.poll.getId());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getThanksMessage());
        this.response.appendInt(this.poll.getQuestions().size());
        for (PollQuestion question : this.poll.getQuestions())
        {
            question.serialize(this.response);
        }

        this.response.appendBoolean(true);
        return this.response;
    }
}
