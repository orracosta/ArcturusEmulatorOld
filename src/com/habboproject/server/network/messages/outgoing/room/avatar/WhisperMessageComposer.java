package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class WhisperMessageComposer extends MessageComposer {
    private final int playerId;
    private final String message;
    private final int bubbleId;

    public WhisperMessageComposer(int playerId, String message, int bubbleId) {
        this.playerId = playerId;
        this.message = message;
        this.bubbleId = bubbleId;
    }

    public WhisperMessageComposer(int playerId, String message) {
        this(playerId, message, 0);
    }

    @Override
    public short getId() {
        return Composers.WhisperMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeString(message);
        msg.writeInt(0);
        msg.writeInt(bubbleId);
        msg.writeInt(0);
        msg.writeInt(-1);
    }
}
