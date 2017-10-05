package com.habboproject.server.network.messages.outgoing.messenger;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class UpdateFriendStateMessageComposer extends MessageComposer {
    private final PlayerAvatar playerAvatar;
    private final boolean online;
    private final boolean inRoom;

    private int action;
    private int playerId;

    public UpdateFriendStateMessageComposer(final PlayerAvatar playerAvatar, final boolean online, final boolean inRoom) {
        this.playerAvatar = playerAvatar;
        this.online = online;
        this.inRoom = inRoom;
    }

    public UpdateFriendStateMessageComposer(int action, int playerId) {
        this.playerAvatar = null;
        this.online = false;
        this.inRoom = false;
        this.action = action;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.FriendListUpdateMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (this.playerAvatar == null) {
            msg.writeInt(0);
            msg.writeInt(1);
            msg.writeInt(this.action);
            msg.writeInt(this.playerId);

            return;
        }

        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(0);
        msg.writeInt(this.playerAvatar.getId());
        msg.writeString(this.playerAvatar.getUsername());
        msg.writeInt(1);
        msg.writeBoolean(online);
        msg.writeBoolean(inRoom);
        msg.writeString(this.playerAvatar.getFigure());
        msg.writeInt(0);
        msg.writeString(this.playerAvatar.getMotto());
        msg.writeString(""); // facebook name ?
        msg.writeString("");
        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(false);
        msg.writeBoolean(false);
        msg.writeBoolean(false);
    }
}
