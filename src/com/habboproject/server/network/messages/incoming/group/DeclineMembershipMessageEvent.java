package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupMembersMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.ArrayList;

public class DeclineMembershipMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        int playerId = msg.readInt();

        if (!client.getPlayer().getGroups().contains(groupId))
            return;

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null || group.getData().getOwnerId() != client.getPlayer().getId())
            return;

        if (!group.getMembershipComponent().getMembershipRequests().contains(playerId))
            return;

        group.getMembershipComponent().removeRequest(playerId);

        client.send(new GroupMembersMessageComposer(group.getData(), 0, new ArrayList<>(group.getMembershipComponent().getMembershipRequests()), 2, "", true));
    }
}
