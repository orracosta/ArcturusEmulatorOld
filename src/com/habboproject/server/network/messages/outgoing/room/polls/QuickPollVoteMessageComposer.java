package com.habboproject.server.network.messages.outgoing.room.polls;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class QuickPollVoteMessageComposer extends MessageComposer {

    private final int playerId;
    private final String myVote;

    private final int yesVotesCount;
    private final int noVotesCount;

    public QuickPollVoteMessageComposer(int playerId, String myVote, int yesVotesCount, int noVotesCount) {
        this.playerId = playerId;
        this.myVote = myVote;
        this.yesVotesCount = yesVotesCount;
        this.noVotesCount = noVotesCount;
    }

    @Override
    public short getId() {
        return Composers.QuickPollResultMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerId);
        msg.writeString(myVote);
        msg.writeInt(2);
        msg.writeString("1");
        msg.writeInt(this.yesVotesCount);

        msg.writeString("0");
        msg.writeInt(this.noVotesCount);
    }
}
