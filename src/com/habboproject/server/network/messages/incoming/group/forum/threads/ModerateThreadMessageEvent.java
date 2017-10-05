package com.habboproject.server.network.messages.incoming.group.forum.threads;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumPermission;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumUpdateThreadMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.groups.GroupForumThreadDao;

public class ModerateThreadMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        int threadId = msg.readInt();
        int state = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);
        if (group == null || !group.getData().hasForum()) {
            return;
        }

        ForumSettings forumSettings = group.getForumComponent().getForumSettings();
        if (forumSettings.getModeratePermission() == ForumPermission.OWNER ? client.getPlayer().getId() != group.getData().getOwnerId() : !group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())) {
            return;
        }

        ForumThread forumThread = group.getForumComponent().getForumThreads().get(threadId);
        if (forumThread == null) {
            return;
        }

        forumThread.setState(state);

        forumThread.setDeleterId(state != 20 ? 0 : client.getPlayer().getId());
        forumThread.setDeleterTime(state != 20 ? 0 : (int) Comet.getTime());

        GroupForumThreadDao.saveMessageState(forumThread.getId(), state, state != 20 ? 0 : client.getPlayer().getId(), state != 20 ? 0 : (int) Comet.getTime());

        client.send(new NotificationMessageComposer(state == 20 ? "forums.thread.hidden" : "forums.thread.restored"));
        client.send(new GroupForumUpdateThreadMessageComposer(groupId, forumThread, client.getPlayer().getId()));

    }
}
