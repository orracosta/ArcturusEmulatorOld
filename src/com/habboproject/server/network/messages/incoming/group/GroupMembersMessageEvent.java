package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupMembersMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;

import java.util.ArrayList;
import java.util.List;


public class GroupMembersMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int groupId = msg.readInt();
        final int page = msg.readInt();
        final String searchQuery = msg.readString();
        final int requestType = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null)
            return;

        List<Object> groupMembers;

        switch (requestType) {
            default:
                groupMembers = new ArrayList<>(group.getMembershipComponent().getMembersAsList());
                break;
            case 1:
                groupMembers = new ArrayList<>(group.getMembershipComponent().getAdministrators());
                break;
            case 2:
                groupMembers = new ArrayList<>(group.getMembershipComponent().getMembershipRequests());
                break;
        }

        if (!searchQuery.isEmpty()) {
            List<Object> toRemove = new ArrayList<>();

            for (Object obj : groupMembers) {
                String username = PlayerDao.getUsernameByPlayerId(obj instanceof GroupMember ? ((GroupMember) obj).getPlayerId() : (int) obj);

                if (username == null) {
                    toRemove.add(obj);
                } else {
                    if (!username.toLowerCase().startsWith(searchQuery.toLowerCase()))
                        toRemove.add(obj);
                }
            }

            for (Object obj : toRemove) {
                groupMembers.remove(obj);
            }

            toRemove.clear();
        }

        client.send(new GroupMembersMessageComposer(group.getData(), page, groupMembers, requestType, searchQuery, group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())));
    }
}
