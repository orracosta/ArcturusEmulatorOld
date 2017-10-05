package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class WallItemsMessageComposer extends MessageComposer {
    private final Room room;

    public WallItemsMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.ItemsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        int size = room.getItems().getWallItems().size();

        if (size > 0) {
            if (room.getGroup() == null) {
                msg.writeInt(1);
                msg.writeInt(room.getData().getOwnerId());
                msg.writeString(room.getData().getOwner());
            } else {
                final Group group = room.getGroup();

                if (group.getData().canMembersDecorate()) {
                    msg.writeInt(group.getMembershipComponent().getMembers().size());

                    for (GroupMember groupMember : group.getMembershipComponent().getMembers().values()) {
                        msg.writeInt(groupMember.getPlayerId());
                        msg.writeString(PlayerDao.getUsernameByPlayerId(groupMember.getPlayerId()));
                    }
                } else {
                    msg.writeInt(group.getMembershipComponent().getAdministrators().size());

                    for (Integer groupMember : group.getMembershipComponent().getAdministrators()) {
                        msg.writeInt(groupMember);
                        msg.writeString(PlayerDao.getUsernameByPlayerId(groupMember));
                    }
                }
            }

        } else {
            msg.writeInt(0);
        }

        msg.writeInt(size);

        for (RoomItemWall item : room.getItems().getWallItems().values()) {
            msg.writeString(item.getVirtualId());
            msg.writeInt(item.getDefinition().getSpriteId());
            msg.writeString(item.getWallPosition());
            msg.writeString(item.getExtraData());
            msg.writeInt(0);
            msg.writeInt(room.getData().getOwnerId());
            msg.writeInt(0);
        }
    }
}
