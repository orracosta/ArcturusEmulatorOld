package com.habboproject.server.network.messages.outgoing.group.forums;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class GroupForumThreadsMessageComposer extends MessageComposer {
    private final int groupId;
    private final List<ForumThread> threads;
    private final int startIndex;
    private final int playerId;

    public GroupForumThreadsMessageComposer(int groupId, List<ForumThread> threads, int startIndex, int playerId) {
        this.groupId = groupId;
        this.threads = threads;
        this.startIndex = startIndex;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.ThreadsListDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        Group group = GroupManager.getInstance().get(this.groupId);

        if (group != null) {
            msg.writeInt(this.groupId);
            msg.writeInt(this.startIndex);
            msg.writeInt(this.threads.size());

            for (ForumThread forumThread : this.threads) {
                forumThread.compose(group.getForumComponent().getUnreadThreadReplies(this.playerId, forumThread.getId()), msg);
            }
        }
    }

    @Override
    public void dispose() {
        this.threads.clear();
    }
}