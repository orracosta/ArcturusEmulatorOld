package com.habboproject.server.network.messages.outgoing.room.polls;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

public class QuickPollResultsMessageComposer extends MessageComposer {

    private final int yesVotesCount;
    private final int noVotesCount;

    public QuickPollResultsMessageComposer(int yesVotesCount, int noVotesCount) {
        this.yesVotesCount = yesVotesCount;
        this.noVotesCount = noVotesCount;
    }

    @Override
    public short getId() {
        return 2498;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(-1);
        msg.writeInt(2);
        msg.writeString("1");
        msg.writeInt(this.yesVotesCount);

        msg.writeString("0");
        msg.writeInt(this.noVotesCount);
    }
}
