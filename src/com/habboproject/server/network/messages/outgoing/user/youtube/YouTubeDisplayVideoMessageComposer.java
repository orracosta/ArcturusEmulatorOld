package com.habboproject.server.network.messages.outgoing.user.youtube;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class YouTubeDisplayVideoMessageComposer extends MessageComposer {
    private final int itemId;
    private final String videoId;
    private final int videoLength;

    public YouTubeDisplayVideoMessageComposer(final int itemId, final String videoId, final int videoLength) {
        this.itemId = itemId;
        this.videoId = videoId;
        this.videoLength = videoLength;
    }

    @Override
    public short getId() {
        return Composers.YouTubeDisplayVideoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(itemId);
        msg.writeString(videoId);
        msg.writeInt(0);
        msg.writeInt(videoLength);
        msg.writeInt(0);
    }
}
