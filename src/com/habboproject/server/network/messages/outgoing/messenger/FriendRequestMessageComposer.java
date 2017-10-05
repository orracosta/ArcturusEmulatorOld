package com.habboproject.server.network.messages.outgoing.messenger;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class FriendRequestMessageComposer extends MessageComposer {
    private final PlayerAvatar playerAvatar;

    public FriendRequestMessageComposer(final PlayerAvatar playerAvatar) {
        this.playerAvatar = playerAvatar;
    }

    @Override
    public short getId() {
        return Composers.NewBuddyRequestMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerAvatar.getId());
        msg.writeString(this.playerAvatar.getUsername());
        msg.writeString(this.playerAvatar.getFigure());
    }
}
