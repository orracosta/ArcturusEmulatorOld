package com.habboproject.server.network.messages.outgoing.group;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.habboproject.server.storage.queries.player.PlayerDao;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GroupInformationMessageComposer extends MessageComposer {
    private final Group group;
    private final RoomData roomData;
    private final boolean flag;
    private final boolean isOwner;
    private final boolean isAdmin;
    private final int membership;

    public GroupInformationMessageComposer(final Group group, final RoomData roomData, final boolean flag, final boolean isOwner, final boolean isAdmin, final int membership) {
        this.group = group;
        this.roomData = roomData;
        this.flag = flag;
        this.isOwner = isOwner;
        this.isAdmin = isAdmin;
        this.membership = membership;
    }

    @Override
    public short getId() {
        return Composers.GroupInfoMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(group.getId());
        msg.writeBoolean(true); //is visible
        msg.writeInt(group.getData().getType().getTypeId());
        msg.writeString(group.getData().getTitle());
        msg.writeString(group.getData().getDescription());
        msg.writeString(group.getData().getBadge());
        msg.writeInt(roomData == null ? 0 : roomData.getId());
        msg.writeString(roomData == null ? "Unknown Room" : roomData.getName());
        msg.writeInt(membership);
        msg.writeInt(group.getMembershipComponent().getMembers().size());
        msg.writeBoolean(false);
        msg.writeString(getDate(group.getData().getCreatedTimestamp()));
        msg.writeBoolean(isOwner);
        msg.writeBoolean(isAdmin);

        msg.writeString(PlayerDao.getUsernameByPlayerId(group.getData().getOwnerId()));

        msg.writeBoolean(flag);
        msg.writeBoolean(group.getData().canMembersDecorate());

        msg.writeInt((isOwner || isAdmin) ? group.getMembershipComponent().getMembershipRequests().size() : 0);
        msg.writeBoolean(group.getData().hasForum());
    }

    public static String getDate(int timestamp) {
        Date d = new Date(timestamp * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        return df.format(d);
    }
}
