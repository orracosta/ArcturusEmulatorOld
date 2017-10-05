package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class FloorItemsMessageComposer extends MessageComposer {
    private final Room room;

    public FloorItemsMessageComposer(final Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.ObjectsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (room.getItems().getFloorItems().size() > 0) {
            if (room.getGroup() == null) {
                msg.writeInt(1);
                msg.writeInt(room.getData().getOwnerId());
                msg.writeString(room.getData().getOwner());
            } else {
                final Group group = room.getGroup();

                if (group.getData().canMembersDecorate()) {
                    msg.writeInt(group.getMembershipComponent().getMembers().size() + 1);

                    msg.writeInt(room.getData().getOwnerId());
                    msg.writeString(room.getData().getOwner());

                    for (GroupMember groupMember : group.getMembershipComponent().getMembers().values()) {
                        msg.writeInt(groupMember.getPlayerId());
                        msg.writeString(PlayerDao.getUsernameByPlayerId(groupMember.getPlayerId()));
                    }
                } else {
                    msg.writeInt(group.getMembershipComponent().getAdministrators().size() + 1);

                    msg.writeInt(room.getData().getOwnerId());
                    msg.writeString(room.getData().getOwner());

                    for (Integer groupMember : group.getMembershipComponent().getAdministrators()) {
                        msg.writeInt(groupMember);
                        msg.writeString(PlayerDao.getUsernameByPlayerId(groupMember));
                    }
                }
            }

            msg.writeInt(room.getItems().getFloorItems().size());

            for (RoomItemFloor item : room.getItems().getFloorItems().values()) {
                item.serialize((msg));
            }
        } else {
            msg.writeInt(0);
            msg.writeInt(0);
        }

    }
}
