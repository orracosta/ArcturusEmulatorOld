package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupAccessLevel;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.groups.types.GroupType;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupBadgesMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreControllerMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class JoinGroupMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();

        if (client.getPlayer().getGroups().contains(groupId)) {
            // Already joined, what you doing??
            return;
        }

        if(client.getPlayer().getGroups().size() >= 100) {
            return;
        }

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null || group.getData().getType() == GroupType.PRIVATE) {
            return;
        }

        if (group.getData().getType() == GroupType.REGULAR) {
            if (client.getPlayer().getData().getFavouriteGroup() == 0) {
                client.getPlayer().getData().setFavouriteGroup(groupId);
                client.getPlayer().getData().save();

                if (client.getPlayer().getEntity() != null) {
                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new GroupBadgesMessageComposer(groupId, group.getData().getBadge()));
                }
            }

            client.getPlayer().getGroups().add(groupId);

            group.getMembershipComponent().createMembership(new GroupMember(client.getPlayer().getId(), group.getId(), GroupAccessLevel.MEMBER));
            client.send(group.composeInformation(true, client.getPlayer().getId()));

            if (client.getPlayer().getEntity() != null && group.getData().canMembersDecorate()) {
                client.getPlayer().getEntity().removeStatus(RoomEntityStatus.CONTROLLER);
                client.getPlayer().getEntity().addStatus(RoomEntityStatus.CONTROLLER, "1");

                client.getPlayer().getEntity().markNeedsUpdate();
                client.send(new YouAreControllerMessageComposer(1));
            }
        } else {
            group.getMembershipComponent().createRequest(client.getPlayer().getId());
            client.send(group.composeInformation(true, client.getPlayer().getId()));
        }
    }
}
