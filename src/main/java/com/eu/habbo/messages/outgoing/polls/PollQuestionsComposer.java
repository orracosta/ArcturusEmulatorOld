package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollQuestion;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-11-2014 18:45.
 */
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

        this.response.appendInt32(this.poll.getId());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getThanksMessage());
        this.response.appendInt32(this.poll.getQuestions().size());

        for (int i = 1; i <= this.poll.getQuestions().size(); i++)
        {
            PollQuestion set = this.poll.getQuestions().get(i);

            this.response.appendInt32(set.getId());
            this.response.appendInt32(0); //SortOrder
            this.response.appendInt32(set.getType());
            this.response.appendString(set.getQuestion());
            this.response.appendInt32(0); //IDK
            this.response.appendInt32(set.getMinSelections());
            this.response.appendInt32(set.getOptions().size());
            if (set.getType() == 1 || set.getType() == 2)
            {
                int j = 0;
                for (String[] strings : set.getOptions().values())
                {
                    this.response.appendString(strings[0]);
                    this.response.appendString(strings[1]);
                    this.response.appendInt32(j);
                    j++;
                }
            }
            this.response.appendInt32(0); //Subquestions
        }
        this.response.appendBoolean(true);
        return this.response;
    }
}
