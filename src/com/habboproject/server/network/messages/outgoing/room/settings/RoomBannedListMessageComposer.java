package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.components.types.RoomBan;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;


public class RoomBannedListMessageComposer extends MessageComposer {
    private final int roomId;
    private final List<RoomBan> bans;

    public RoomBannedListMessageComposer(int roomId, List<RoomBan> bans) {
        this.roomId = roomId;
        this.bans = bans;
    }

    @Override
    public short getId() {
        return Composers.GetRoomBannedUsersMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(roomId);
        msg.writeInt(bans.size());

        for (RoomBan ban : bans) {
            msg.writeInt(ban.getPlayerId());
            msg.writeString(ban.getPlayerName());
        }
    }
}
