package com.habboproject.server.network.messages.outgoing.group;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class GroupRoomMessageComposer extends MessageComposer {
    private final int roomId;
    private final int groupId;

    public GroupRoomMessageComposer(final int roomId, final int groupId) {
        this.roomId = roomId;
        this.groupId = groupId;
    }

    @Override
    public short getId() {
        return Composers.NewGroupInfoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(roomId);
        msg.writeInt(groupId);
    }
}
