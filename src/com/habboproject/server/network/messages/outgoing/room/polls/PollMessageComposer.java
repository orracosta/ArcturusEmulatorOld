package com.habboproject.server.network.messages.outgoing.room.polls;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.game.polls.types.PollQuestion;
import com.habboproject.server.game.polls.types.questions.MultipleChoiceQuestion;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Map;

public class PollMessageComposer extends MessageComposer {

    private final Poll poll;

    public PollMessageComposer(final Poll poll) {
        this.poll = poll;
    }

    @Override
    public short getId() {
        return Composers.PollMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.poll.getPollId());
        msg.writeString(this.poll.getPollTitle());
        msg.writeString(this.poll.getThanksMessage());
        msg.writeInt(this.poll.getPollQuestions().size());

        for(Map.Entry<Integer, PollQuestion> pollQuestion : this.poll.getPollQuestions().entrySet()) {
            msg.writeInt(pollQuestion.getKey());
            msg.writeInt(0);
            msg.writeInt(pollQuestion.getValue() instanceof MultipleChoiceQuestion ? 1 : 3);//type
            msg.writeString(pollQuestion.getValue().getQuestion());
            msg.writeInt(0);

            int minimumSelections = pollQuestion.getValue() instanceof MultipleChoiceQuestion ? 1 : 0;
            int optionSizes = pollQuestion.getValue() instanceof MultipleChoiceQuestion ? ((MultipleChoiceQuestion) pollQuestion.getValue()).getChoices().size() : 0;

            msg.writeInt(minimumSelections);
            msg.writeInt(optionSizes);

            if(optionSizes != 0) {
                for(int i = 0; i < optionSizes; i++) {
                    String choice = ((MultipleChoiceQuestion) pollQuestion.getValue()).getChoices().get(i);

                    msg.writeString("");
                    msg.writeString(choice);
                    msg.writeInt(i);
                }
            }

            msg.writeInt(0);
        }

        msg.writeBoolean(true);
    }
}
