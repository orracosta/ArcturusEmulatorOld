package com.habboproject.server.network.messages.incoming.group.forum.threads;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumPermission;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumPostReplyMessageComposer;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumPostThreadMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.groups.GroupForumThreadDao;

public class PostMessageMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int groupId = msg.readInt();
        final int threadId = msg.readInt();
        String subject = msg.readString();
        String message = msg.readString();

        final Group group = GroupManager.getInstance().get(groupId);

        if (group == null || !group.getData().hasForum()) {
            return;
        }

        if (message.length() < 10 || message.length() > 4000) return;

        if(client.getPlayer().getLastForumPost() != 0) {
            if((((int) Comet.getTime()) - client.getPlayer().getLastForumPost()) < 60) {
                return;
            }
        }

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult messageFilter = RoomManager.getInstance().getFilter().filter(message);

            if (messageFilter.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", messageFilter.getMessage())));
                return;
            } else if (messageFilter.wasModified()) {
                message = messageFilter.getMessage();
            }

            FilterResult subjectFilter = RoomManager.getInstance().getFilter().filter(subject);

            if (subjectFilter.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", subjectFilter.getMessage())));
                return;
            } else if (subjectFilter.wasModified()) {
                subject = subjectFilter.getMessage();
            }
        }


        final ForumSettings forumSettings = group.getForumComponent().getForumSettings();

        if (threadId == 0) {

            if (subject.length() < 10) {
                return;
            }

            boolean permissionToPost = true;

            if (forumSettings.getStartThreadsPermission() != ForumPermission.EVERYBODY) {
                switch (forumSettings.getStartThreadsPermission()) {
                    case ADMINISTRATORS:
                        if (!group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())) {
                            permissionToPost = false;
                        }
                        break;

                    case OWNER:
                        if (client.getPlayer().getId() != group.getData().getOwnerId()) {
                            permissionToPost = false;
                        }
                        break;

                    case MEMBERS:
                        if (!group.getMembershipComponent().getMembers().containsKey(client.getPlayer().getId())) {
                            permissionToPost = false;
                        }
                }
            }

            if (!permissionToPost) {
                // No permission notif?
                return;
            }

            ForumThread forumThread = GroupForumThreadDao.createThread(groupId, subject, message, client.getPlayer().getId());

            if(forumThread == null) {
                // Why u do dis?
                return;
            }

            group.getForumComponent().getForumThreads().put(forumThread.getId(), forumThread);
            client.send(new GroupForumPostThreadMessageComposer(groupId, forumThread));

            client.getPlayer().setLastForumPost((int) Comet.getTime());
            client.getPlayer().getData().increaseForumPosts();
        } else {
            boolean permissionToPost = true;

            if (forumSettings.getPostPermission() != ForumPermission.EVERYBODY) {
                switch (forumSettings.getPostPermission()) {
                    case ADMINISTRATORS:
                        if (!group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())) {
                            permissionToPost = false;
                        }
                        break;

                    case OWNER:
                        if (client.getPlayer().getId() != group.getData().getOwnerId()) {
                            permissionToPost = false;
                        }
                        break;

                    case MEMBERS:
                        if (!group.getMembershipComponent().getMembers().containsKey(client.getPlayer().getId())) {
                            permissionToPost = false;
                        }
                }
            }

            if(!permissionToPost) {
                // No permission notif?
                return;
            }

            ForumThread forumThread = group.getForumComponent().getForumThreads().get(threadId);

            if(forumThread == null) {
                return;
            }

            ForumThreadReply reply = GroupForumThreadDao.createReply(groupId, threadId, message, client.getPlayer().getId());

            if(reply == null) {
                return;
            }

            forumThread.addReply(reply);
            reply.setIndex(forumThread.getReplies().indexOf(reply));

            // Send to client.
            client.send(new GroupForumPostReplyMessageComposer(groupId, threadId, reply));
            client.getPlayer().setLastForumPost((int) Comet.getTime());
            client.getPlayer().getData().increaseForumPosts();
        }
    }
}
