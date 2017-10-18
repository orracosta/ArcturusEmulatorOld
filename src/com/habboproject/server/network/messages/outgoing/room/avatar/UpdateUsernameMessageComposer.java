package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class UpdateUsernameMessageComposer extends MessageComposer {
    private final int roomId;
    private final int virtualId;
    private final String username;

    public UpdateUsernameMessageComposer(final int roomId, final int virtualId, final String username) {
        this.roomId = roomId;
        this.virtualId = virtualId;
        this.username = username;
    }

    @Override
    public short getId() {
        return Composers.UpdateUsernameMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(roomId);
        msg.writeInt(virtualId);
        msg.writeString(username);
    }
}
