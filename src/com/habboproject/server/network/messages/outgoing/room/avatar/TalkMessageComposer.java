package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class TalkMessageComposer extends MessageComposer {
    private final int playerId;
    private final String message;
    private final ChatEmotion emoticon;
    private final int colour;

    public TalkMessageComposer(final int playerId, final String message, final ChatEmotion emoticion, final int colour) {
        this.playerId = playerId;
        this.message = message;
        this.emoticon = emoticion;
        this.colour = colour;
    }

    @Override
    public short getId() {
        return Composers.ChatMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeString(message);
        msg.writeInt(emoticon.getEmotionId());
        msg.writeInt(colour);
        msg.writeInt(0);
        msg.writeInt(-1);
    }
}
