package com.habboproject.server.network.messages.outgoing.user.achievements;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class AchievementPointsMessageComposer extends MessageComposer {
    private final int points;

    public AchievementPointsMessageComposer(final int points) {
        this.points = points;
    }

    @Override
    public short getId() {
        return Composers.AchievementScoreMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.points);
    }
}
