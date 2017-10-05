package com.habboproject.server.network.messages.incoming.group.forum.threads;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumPermission;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumThreadsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ForumThreadsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();

        int start = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);

        if(group == null) {
            return;
        }

        if(group.getForumComponent().getForumSettings().getReadPermission() == ForumPermission.MEMBERS) {
            if(!group.getMembershipComponent().getMembers().containsKey(client.getPlayer().getId())) {
                return;
            }
        } else if(group.getForumComponent().getForumSettings().getReadPermission() == ForumPermission.ADMINISTRATORS) {
            if(!group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())) {
                return;
            }
        }

        client.send(new GroupForumThreadsMessageComposer(group.getId(), group.getForumComponent().getForumThreads(start), start,
                client.getPlayer().getId()));
    }
}
