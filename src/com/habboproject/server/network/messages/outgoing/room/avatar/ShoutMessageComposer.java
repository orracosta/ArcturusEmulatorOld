package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.protocol.headers.Composers;


public class ShoutMessageComposer extends TalkMessageComposer {

    public ShoutMessageComposer(final int playerId, final String message, final ChatEmotion emotion, final int colour) {
        super(playerId, message, emotion, colour);
    }

    @Override
    public short getId() {
        return Composers.ShoutMessageComposer;
    }
}
