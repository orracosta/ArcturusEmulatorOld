package com.habboproject.server.network.messages.incoming.group.settings;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupAccessLevel;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ModifyGroupTitleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        String title = msg.readString();
        String description = msg.readString();

        if (!client.getPlayer().getGroups().contains(groupId))
            return;

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null)
            return;

        GroupMember groupMember = group.getMembershipComponent().getMembers().get(client.getPlayer().getId());

        if (groupMember.getAccessLevel() != GroupAccessLevel.OWNER)
            return;

        group.getData().setTitle(title);
        group.getData().setDescription(description);
        group.getData().save();

        if (RoomManager.getInstance().isActive(group.getData().getRoomId())) {
            Room room = RoomManager.getInstance().get(group.getData().getRoomId());

            room.getEntities().broadcastMessage(new RoomDataMessageComposer(room));
        }
    }
}
