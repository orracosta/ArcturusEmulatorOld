package com.habboproject.server.network.messages.outgoing.room.queue;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

public class RoomQueueStatusMessageComposer extends MessageComposer {

    private final int playersWaiting;
    private final int playersSpectating;

    public RoomQueueStatusMessageComposer(int playersWaiting, int playersSpectating) {
        this.playersWaiting = playersWaiting;
        this.playersSpectating = playersSpectating;
    }

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(2);
        {
            msg.writeString("visitors");
            msg.writeInt(2);

            msg.writeInt(1);
            {
                msg.writeString("visitors");
                msg.writeInt(this.playersWaiting);
            }

            msg.writeString("spectators");
            msg.writeInt(1);

            msg.writeInt(1);
            {
                msg.writeString("spectators");
                msg.writeInt(this.playersSpectating);
            }
        }

    }
}
