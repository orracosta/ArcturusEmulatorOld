package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomRatingMessageComposer extends MessageComposer {
    private final int score;
    private final boolean canRate;

    public RoomRatingMessageComposer(int score, boolean canRate) {
        this.score = score;
        this.canRate = canRate;
    }

    @Override
    public short getId() {
        return Composers.RoomRatingMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.score);
        msg.writeBoolean(this.canRate);
    }
}
